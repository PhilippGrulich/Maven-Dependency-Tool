package org.haw.mavenDependencyTool.neo4JImporter;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Neo4JImportWorker {

	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> collection;

	public Neo4JImportWorker() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("a5");
		collection = database.getCollection("poms");
	}

	public List<String> getMongoData() {
		final List<String> list = new ArrayList<String>();

		collection.find(new Document()).limit(10).forEach(new Block<Document>() {
			public void apply(final Document document) {
				System.out.println(document.toJson());
				// System.out.println(document.getString("project.parent.groupId"));
				// list.add("project.version: " +
				// document.getString("project:"));
			}
		});

		// BasicDBObject query = new BasicDBObject("project",
		// "{modelVersion: 4.0.0, groupId : mule, artifactId :
		// mule-extras-picocontainer, version : 1.1.1}");
		//
		// FindIterable<Document> cursor = collection.find(query);
		//
		// // Document query = new Document("project:", new Document());
		// // FindIterable<Document> search = collection.find(query).limit(10);
		// for (Document item : cursor) {
		// System.out.println(item);
		// }

//		BasicDBObject query = new BasicDBObject("project", new BasicDBObject("modelVersion", new BasicDBObject()));
//		BasicDBObject query = new BasicDBObject("project", new BasicDBObject());
//		BasicDBObject query = new BasicDBObject("_id", new BasicDBObject("oid", new BasicDBObject()));

		BasicDBObject query = new BasicDBObject();
//		BasicDBObject query = new BasicDBObject(); // because you have no conditions
//		BasicDBObject fields = new BasicDBObject("Name",1);
//		collection.find(query, fields);
		
		System.out.println("q: " + query);
		FindIterable<Document> cursor = collection.find(query
		/* new BasicDBObject("project", new BasicDBObject()) */).limit(10);
		MongoCursor<Document> a = cursor.iterator();

		while (a.hasNext()) {
			System.out.println(a.next().get("project"));
//			System.out.println(a.next().get("groupId"));

		}
		a.close();
		return list;

	}

	public void close() {
		if (mongoClient != null)
			mongoClient.close();
	}

}
