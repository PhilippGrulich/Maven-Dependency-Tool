package org.haw.mavenDependencyTool.neo4JImporter;

public class Project {
	Item parent;
	String artifactId;
	String name;
	String version;
	String groupId;
	String description;
	Dependencies dependencies;
	
	public Project(Item parent, String artifactId, String name, String version,
			String groupId, String description, Dependencies dependencies) {
		super();
		this.parent = parent;
		this.artifactId = artifactId;
		this.name = name;
		this.version = version;
		this.groupId = groupId;
		this.description = description;
		this.dependencies = dependencies;
	}

	public Item getParent() {
		return parent;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getGroupID() {
		return groupId;
	}

	public String getDescription() {
		return description;
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

	@Override
	public String toString() {
		return "Project [parent=" + parent + ", artifactId=" + artifactId
				+ ", name=" + name + ", version=" + version + ", groupId="
				+ groupId + ", description=" + description + ", dependencies="
				+ dependencies + "]";
	}
	
	
}
