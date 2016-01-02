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

	PrintWriter versions;
	PrintWriter groups;
	HashSet<String> groupIDSet;
	PrintWriter artifacts;
	HashSet<String> artifactsIdSet;
	PrintWriter links;
	HashSet<String> linksSet = new HashSet<>();
	long startDate = System.currentTimeMillis();

	private PrintWriter data;

	private HashSet<String> versionSet;

	Neo4JImportCSV() {
		try {

			// groups = new PrintWriter(new BufferedWriter(new
			// FileWriter("data/groups.csv", false)));
			// groups.println("id:ID,name");
			// versions = new PrintWriter(new BufferedWriter(new
			// FileWriter("data/version.csv", false)));
			// versions.println("id:ID;name");
			//
			// artifacts = new PrintWriter(new BufferedWriter(new
			// FileWriter("data/artifacts.csv", false)));
			// artifacts.println("id:ID;name");
			data = new PrintWriter(new BufferedWriter(new FileWriter("data/data.csv", false)));
			// data.println("id:ID;name;:LABEL");
			artifactsIdSet = new HashSet<>();
			groupIDSet = new HashSet<>();
			versionSet = new HashSet<>();

			links = new PrintWriter(new BufferedWriter(new FileWriter("data/links.csv", false)));
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
			if (counter % 100 == 0) {
				System.out.println("found " + counter + " new objects");

				System.out.println(System.currentTimeMillis() - startDate);
				startDate = System.currentTimeMillis();
			}
			try {
				if (project.getVersion() != null) {
					createVersion(project);
					createArtifact(project);
					createGroup(project);
					createArtifactVersionRelation(project);
					createGroupVersionRelation(project);
					createDependencyRelation(project);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		// versions.flush();
		// artifacts.flush();
		// groups.flush();
		links.flush();
		data.flush();
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

	private void createGroup(Project project) throws SQLException {
		String groupid = project.getGroupId();
		addGroup(groupid);
	}

	private void createArtifactVersionRelation(Project project) throws SQLException {
		String artifactKey = getArtifactKey(project);
		String versionKey = getVersionKey(project, project.getVersion());
		String linkKey = project.id + "_" + artifactKey;
		if (!linksSet.contains(linkKey)) {
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s", versionKey, artifactKey, "gehoert_zu"));
		}

	}

	private void createGroupVersionRelation(Project project) throws SQLException {
		String artifactKey = getArtifactKey(project);
		String linkKey = artifactKey + "_" + project.getGroupId();
		if (!linksSet.contains(linkKey)) {
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s", artifactKey, project.getGroupId(), "gehoert_zu"));
		}
	}

	private void createDependencyRelation(Project project) throws SQLException {

		List<Item> deps = project.getDependency();
		if (deps != null) {
			String versionKey = getVersionKey(project, project.getVersion());
			deps.forEach(dependency -> {

				String dependencyVersion = "";
				if (dependency.version == null || dependency.version.toString().contains("$")) {
					dependencyVersion = project.getVersion();
				} else {
					dependencyVersion = dependency.getVersion();
				}

				if (dependency.groupId == null) {

					dependency.groupId = project.getGroupId();
				}

				String dependencyVersionKey = "Version_" + dependency.groupId + "_" + dependency.artifactId + "_"
						+ dependencyVersion;
				addVersion(dependencyVersion, dependencyVersionKey);
				String linkKey = versionKey + "->" + dependencyVersionKey;
				if (!linksSet.contains(linkKey)) {
					linksSet.add(linkKey);
					links.println(String.format("%s;%s;%s", versionKey, dependencyVersionKey, "nutzt"));
				}

			});

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
