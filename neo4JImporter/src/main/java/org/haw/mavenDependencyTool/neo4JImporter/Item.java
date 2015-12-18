package org.haw.mavenDependencyTool.neo4JImporter;

import com.google.gson.JsonElement;

public class Item {
	String artifactId;
	JsonElement version;
	String groupId;
	public Item(String artifactId, JsonElement version, String groupId) {
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
	
	public String getVersion(){
		if(version != null && version.isJsonPrimitive())
			return version.getAsString();
		else if(version != null){
			System.out.println(this.toString());
			System.out.println(version.getAsJsonObject().toString());
			if(version.getAsJsonObject().has("content")){
				return version.getAsJsonObject().get("content").getAsString();
			}else
			return version.getAsJsonObject().get("$numberLong").getAsString();
		}
		
		return "";
	}
	
	
}
