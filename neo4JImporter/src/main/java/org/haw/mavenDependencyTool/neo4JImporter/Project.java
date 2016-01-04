package org.haw.mavenDependencyTool.neo4JImporter;

import java.util.ArrayList;
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
	JsonElement dependencyManagement;

	public Project(Item parent, String artifactId, String name,
			JsonElement version, String groupId, String description,
			JsonElement dependencies, JsonElement dependencyManagement) {
		super();
		this.parent = parent;
		this.artifactId = artifactId;
		this.name = name;
		this.version = version;
		this.groupId = groupId;
		//this.description = description;
		this.dependencies = dependencies;
		this.dependencyManagement = dependencyManagement;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", parent=" + parent + ", artifactId="
				+ artifactId + ", name=" + name + ", version=" + version
				+ ", groupId=" + groupId + ", description=" + description
				+ ", dependencies=" + dependencies + ", dependencyManagement="
				+ dependencyManagement + "]";
	}

	public String getVersion() {
		if (version != null && version.isJsonPrimitive())
			return version.getAsString();
		else if (version != null) {
			if (version.getAsJsonObject().has("$numberLong"))
				return version.getAsJsonObject().get("$numberLong")
						.getAsString();
			if (version.getAsJsonObject().has("content"))
				return version.getAsJsonObject().get("content").getAsString();
			else {
				System.err.println("Not known field " + version);
			}
		} else if (parent != null)
			return parent.getVersion();
		return null;
	}

	public String getGroupId() {
		if (groupId != null)
			return groupId;
		return parent.groupId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Item> getDependency() {
		if (this.dependencyManagement != null) {
			if (this.dependencyManagement.isJsonObject()) {
				this.dependencies = this.dependencyManagement.getAsJsonObject()
						.get("dependencies");
			} else if(this.dependencyManagement.isJsonPrimitive()){
				
			} 
			else
				System.out.println("Fehler:" + this.toString());
		}
		if (this.dependencies == null)
			return null;
		if (this.dependencies.isJsonPrimitive()
				&& this.dependencies.getAsString().isEmpty()) {
			return null;
		}
		if(this.dependencies.isJsonArray()){
			this.dependencies = this.dependencies.getAsJsonArray().get(0);
		 }
		if (this.dependencies.isJsonObject()) {
			JsonObject obj = this.dependencies.getAsJsonObject();
			if (obj.has("dependency")) {
				JsonElement dep = obj.get("dependency");
				if (dep.isJsonArray()) {
					return new Gson().fromJson(obj, Dependencies.class).dependency;
				} else if (dep.isJsonObject()) {
					List<Item> list = new ArrayList<Item>();
					list.add(new Gson().fromJson(dep, Item.class));
					return list;
				}

			}
		} else {
				
			System.err.println(this.dependencies);
		}
		return null;
	}
}
