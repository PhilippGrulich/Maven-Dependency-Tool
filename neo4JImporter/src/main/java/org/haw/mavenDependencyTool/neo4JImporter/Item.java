package org.haw.mavenDependencyTool.neo4JImporter;

public class Item {
	String artifactId;
	String version;
	String groupId;
	public Item(String artifactId, String version, String groupId) {
		super();
		this.artifactId = artifactId;
		this.version = version;
		this.groupId = groupId;
	}
	@Override
	public String toString() {
		return "Item [artifactId=" + artifactId + ", version=" + version
				+ ", groupId=" + groupId + "]";
	}	
	
	
}
