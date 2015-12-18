package org.haw.mavenDependencyTool.restServer.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MongoObject {
	
	private JsonObject document;

	public MongoObject(JsonObject d) {
		this.document = d;
	}
	
	public MongoObject subDoc(String... parent){
		JsonObject sub = new JsonObject();
		for(int i = 0; i<parent.length;i++){
			sub.add(parent[i], subDoc(parent[i]));
		}
		return new MongoObject(sub);
	}
	private JsonElement subDoc(String parent){
		String[] items = parent.split("/.");
		JsonObject sub = document;
		for (int i=0;i<items.length;i++){		
			if(i==items.length-1){
				return sub.get(items[i]);
			}
			sub = sub.getAsJsonObject(items[i]);
		}
		return sub;
	}
	
	public <T> T asObject( Class<T> classOfT){
		return new Gson().fromJson(document, classOfT);
	}
	
	public String toJson(){
		return document.toString();
	}
	

}
