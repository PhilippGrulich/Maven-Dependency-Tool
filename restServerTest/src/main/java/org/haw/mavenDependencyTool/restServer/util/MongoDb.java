package org.haw.mavenDependencyTool.restServer.util;

import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoDb {
	MongoClient mongoClient = new MongoClient("localhost", 27017);
	MongoDatabase database = mongoClient.getDatabase("a5");
	MongoCollection<Document> collection = database.getCollection("poms");
	
	public MongoDb(){
		
	}
	
	public MongoFind query(String q){
		DBObject dbObject = (DBObject) JSON.parse(q);	
		
		return new MongoFind(collection.find((org.bson.conversions.Bson) dbObject));
	}
}
