package org.haw.mavenDependencyTool.neo4JImporter;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Neo4JImportWorker {

	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> collection;
	GsonBuilder gsonBuilder;
	Gson gson;

	public Neo4JImportWorker() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("a5");
		collection = database.getCollection("poms");
		gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
				new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class,new DependencyAdapater());
		gson = gsonBuilder.create();
	}

	public Stream<Project> getMongoData() {

		Stream<Document> stream = StreamSupport.stream(
				collection.find(new Document()).spliterator(), false);
		return stream.map(document -> {
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(document.toJson()).getAsJsonObject();
			System.out.println(jsonObject.get("project"));
		
			Project project = gson.fromJson(jsonObject.get("project"),Project.class);
//			System.out.println(project);
			
			return project;
		});

		// collection.find(new Document()).limit(10)
		// .forEach(new Block<Document>() {
		// public void apply(final Document document) {
		// JsonParser parser = new JsonParser();
		// JsonObject jsonObject = parser.parse(document.toJson())
		// .getAsJsonObject();
		//
		// Project project = gson.fromJson(
		// jsonObject.get("project"), Project.class);
		// System.out.println(project);
		// }
		// });

		// return stream;

	}

	public void close() {
		if (mongoClient != null)
			mongoClient.close();
	}

}
