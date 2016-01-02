package org.haw.mavenDependencyTool.neo4JImporter;

import java.util.List;

import com.google.gson.Gson;
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
		else if (parent != null)
			return parent.getVersion();
		return null;
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
	
	public List<Item> getDependency(){
		if(this.dependencies==null)
			return null;
		if(this.dependencies.isJsonPrimitive()&&this.dependencies.getAsString().isEmpty()){
			return null;
		}
		if(this.dependencies.isJsonObject()){
			JsonObject obj = this.dependencies.getAsJsonObject();
			if(obj.has("dependency")){
				JsonElement dep = obj.get("dependency");
				if(dep.isJsonArray()){
					return new Gson().fromJson(obj, Dependencies.class).dependency;
				}
			}
		}else{
			
			System.err.println(this.dependencies);
		}
		return null;
	}
	
	
}
