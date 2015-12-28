package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;
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

	Neo4JImportCSV() {
		try {
		
//			groups = new PrintWriter(new BufferedWriter(new FileWriter("data/groups.csv", false)));
//			groups.println("id:ID,name");
//			versions = new PrintWriter(new BufferedWriter(new FileWriter("data/version.csv", false)));
//			versions.println("id:ID;name");
//		
//			artifacts = new PrintWriter(new BufferedWriter(new FileWriter("data/artifacts.csv", false)));
//			artifacts.println("id:ID;name");
			data = new PrintWriter(new BufferedWriter(new FileWriter("data/data.csv", false)));
			//data.println("id:ID;name;:LABEL");
			artifactsIdSet = new HashSet<>();
			groupIDSet = new HashSet<>();
			links = new PrintWriter(new BufferedWriter(new FileWriter("data/links.csv", false)));
			//links.println(":START_ID;:END_ID;:TYPE");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importNeo4J(Stream<Project> stream) {
		stream.forEach(project -> {
			if(project==null)
				return;
			counter++;
			if (counter % 100 == 0) {
				System.out.println("found " + counter + " new objects");

				System.out.println(System.currentTimeMillis() - startDate);
				startDate = System.currentTimeMillis();
			}
			try {
				createVersion(project);
				createArtifact(project);
				createGroup(project);
				createArtifactVersionRelation(project);
				createGroupVersionRelation(project);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
//		versions.flush();
//		artifacts.flush();
//		groups.flush();
		links.flush();
		data.flush();
	}

	private void createVersion(Project project) throws SQLException {
		String version = project.getVersion();		
		data.println(String.format("%s;%s;%s;",project.id,version,"version"));
		
	}

	private void createArtifact(Project project) throws SQLException {
		// Die artifactID ist eindeutig über den key
		String key = project.getGroupId() + "_" + project.artifactId;		
		if(!artifactsIdSet.contains(key)){
			artifactsIdSet.add(key);
			data.println(String.format("%s;%s;%s;",key,project.artifactId,"artifact"));
		}		
	}

	private void createGroup(Project project) throws SQLException {
		String groupid = project.getGroupId();	
		if(!groupIDSet.contains(groupid)){
			groupIDSet.add(groupid);
			data.println(String.format("%s;%s;%s;",groupid,groupid,"group"));
		}
	}

	private void createArtifactVersionRelation(Project project) throws SQLException {
		String artifactKey = project.getGroupId() + "_" + project.artifactId;		
		String linkKey = project.id+"_"+artifactKey;
		if(!linksSet.contains(linkKey)){
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s",project.id,artifactKey,"gehoert_zu"));		
		}
		
	}

	private void createGroupVersionRelation(Project project) throws SQLException {
		String artifactKey = project.getGroupId() + "_" + project.artifactId;			
		String linkKey = artifactKey+"_"+project.getGroupId();
		if(!linksSet.contains(linkKey)){
			linksSet.add(linkKey);
			links.println(String.format("%s;%s;%s",artifactKey,project.getGroupId(),"gehoert_zu"));		
		}
	}

}
