package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class Neo4JImportCSV {

	int counter = 0;
	int error = 0;
	PrintWriter versions;
	PrintWriter groups;
	HashSet<String> groupIDSet;
	PrintWriter artifacts;
	HashSet<String> artifactsIdSet;
	PrintWriter links;

	HashSet<String> linksSet = new HashSet<>();
	PrintWriter errorDependendiesWriter;
	long startDate = System.currentTimeMillis();

	private PrintWriter data;

	private HashSet<String> versionSet;

	Neo4JImportCSV() {
		try {

			data = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/data.csv", false)));
			// data.println("id:ID;name;:LABEL");
			artifactsIdSet = new HashSet<>(1000000);
			groupIDSet = new HashSet<>(1000000);
			versionSet = new HashSet<>(1000000);

			links = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/links.csv", false)));
			errorDependendiesWriter = new PrintWriter(new BufferedWriter(new FileWriter("error.csv", false)));
			// links.println(":START_ID;:END_ID;:TYPE");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importNeo4J(Stream<Project> stream) {
		stream.forEach(project -> {
			if (project == null)
				return;
			counter++;
			if (counter % 500 == 0) {
				System.out.println("found " + counter + " new objects");
				// fixError();
				// System.gc();
				System.out.println("Time Diff: " + (System.currentTimeMillis() - startDate));
				startDate = System.currentTimeMillis();
			}
			try {
				if (project.getVersion() != null && !project.getVersion().contains("$") && project.artifactId != null
						&& project.getGroupId() != null) {
					createVersion(project);
					createArtifact(project);
					createGroup(project);
					createArtifactVersionRelation(project);
					createGroupArtifactRelation(project);
					createDependencyRelation(project);
					// createVersionParentRelation(project);
				} else {
					error++;
					System.err.println("Cant use this: " + project.toString());
				}
			} catch (Exception e) {
				error++;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		links.flush();
		data.flush();
		errorDependendiesWriter.flush();
		System.out.println("Unknown Projects: " + error + " total" + counter);
	}

	private void createVersion(Project project) throws SQLException {
		String version = project.getVersion();
		String key = getVersionKey(project, version);
		addVersion(version, key);

	}

	private String getVersionKey(Project project, String version) {
		String key = "Version_" + project.getGroupId() + "_" + project.artifactId + "_" + version;
		return key;
	}

	private void createArtifact(Project project) throws SQLException {
		// Die artifactID ist eindeutig über den key
		String key = getArtifactKey(project);
		addArtifact(project, key);
	}

	private String getArtifactKey(Project project) {
		String key = "Artifact_" + project.getGroupId() + "_" + project.artifactId;
		return key;
	}

	private void createGroup(Project project) {
		String groupid = project.getGroupId();
		addGroup(groupid);
	}

	private void createArtifactVersionRelation(Project project) {
		String artifactKey = getArtifactKey(project);
		String versionKey = getVersionKey(project, project.getVersion());
		String linkKey = project.id + "_" + artifactKey;
		if (!linksSet.contains(linkKey)) {
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s", versionKey, artifactKey, "gehoert_zu"));
		}

	}

	private void createGroupArtifactRelation(Project project) {
		String artifactKey = getArtifactKey(project);
		String linkKey = artifactKey + "_" + project.getGroupId();
		if (!linksSet.contains(linkKey)) {
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s", artifactKey, project.getGroupId(), "gehoert_zu"));
		}
	}

	private void createVersionParentRelation(Project project) {
		if (project.parent != null && project.parent.groupId != null && project.parent.artifactId != null
				&& project.parent.version != null) {
			String versionKey = getVersionKey(project, project.getVersion());
			String parentVersionKey = "Version_" + project.parent.groupId + "_" + project.parent.artifactId + "_"
					+ project.parent.getVersion();
			String linkKey = (versionKey + "_" + parentVersionKey);
			if (!linkKey.contains("&") && !linksSet.contains(linkKey)) {
				linksSet.add(linkKey);
				links.println(String.format("%s;%s;%s", versionKey, parentVersionKey, "istKindVon"));
			}
		}
	}

	private void createDependencyRelation(Project project) {

		List<Item> deps = project.getDependency();
		if (deps != null) {
			String versionKey = getVersionKey(project, project.getVersion());

			for (Item dependency : deps) {

				String depGroupId = dependency.groupId;
				String depVersion = dependency.getVersion();
				String depArtifactId = dependency.artifactId;

				// Fix for invalid groupId
				if (depGroupId == null) {
					depGroupId = project.getGroupId();
				}

				// Fix for ${project.groupId}
				if (depGroupId.contains("$")) {
					depGroupId = project.getGroupId();
				}

				// fehler in dependency
				if (depArtifactId == null) {
					return;
				}
				if (depArtifactId.contains("$")) {
					depArtifactId = project.artifactId;
				}

				String dependencyVersion = "";
				if (depVersion == null) {
					String tempVersionKey = ("Version_" + depGroupId + "_" + depArtifactId + "_");
					errorDependendiesWriter.println(versionKey + ";" + tempVersionKey);
				} else {

					if (depVersion.contains("$")) {
						dependencyVersion = project.getVersion();
					} else {
						dependencyVersion = dependency.getVersion();
					}

					if (dependencyVersion.contains("$")) {

						String tempVersionKey = ("Version_" + depGroupId + "_" + depArtifactId + "_");
						System.out.println("tempVersionKey: " + tempVersionKey);
						errorDependendiesWriter.println(versionKey + ";" + tempVersionKey);
						return;
					}

					String dependencyVersionKey = "Version_" + depGroupId + "_" + depArtifactId + "_"
							+ dependencyVersion;
					addVersion(dependencyVersion, dependencyVersionKey);
					if (!linksSet.contains(versionKey + "_nutzt_" + dependencyVersionKey)) {
						linksSet.add(versionKey + "_nutzt_" + dependencyVersionKey);
						links.println(String.format("%s;%s;%s", versionKey, dependencyVersionKey, "nutzt"));
					}

				}
			}
		}

	}

	private synchronized void addArtifact(Project project, String key) {
		if (!artifactsIdSet.contains(key)) {
			artifactsIdSet.add(key);
			data.println(String.format("%s;%s;%s;", key, project.artifactId, "artifact"));
		}
	}

	private synchronized void addVersion(String dependencyVersion, String dependencyVersionKey) {
		if (!versionSet.contains(dependencyVersionKey)) {
			versionSet.add(dependencyVersionKey);
			data.println(String.format("%s;%s;%s;", dependencyVersionKey, dependencyVersion, "version"));
		}
	}

	private synchronized void addGroup(String groupid) {
		if (!groupIDSet.contains(groupid)) {
			groupIDSet.add(groupid);
			data.println(String.format("%s;%s;%s;", groupid, groupid, "group"));
		}
	}

}
