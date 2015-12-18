package org.haw.mavenDependencyTool.neo4JImporter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Project {
	String id;
	Item parent;
	String artifactId;
	String name;
	JsonElement version;
	String groupId;
	String description;
	JsonElement dependencies;
	
	public Project(Item parent, String artifactId, String name, JsonElement version,
			String groupId, String description, JsonElement dependencies) {
		super();
		this.parent = parent;
		this.artifactId = artifactId;
		this.name = name;
		this.version = version;
		this.groupId = groupId;
		this.description = description;
		this.dependencies = dependencies;
	}

	@Override
	public String toString() {
		return "Project [parent=" + parent + ", artifactId=" + artifactId
				+ ", name=" + name + ", version=" + version + ", groupId="
				+ groupId + ", description=" + description + ", dependencies="
				+ dependencies + "]";
	}
	
	public String getVersion(){
		if(version != null && version.isJsonPrimitive())
			return version.getAsString();
		else if(version != null)
			return version.getAsJsonObject().get("$numberLong").getAsString();
		else
			return parent.getVersion();
	}
	
	public String getGroupId(){
		if(groupId != null)
			return groupId;
		return parent.groupId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
