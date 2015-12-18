package org.haw.mavenDependencyTool.neo4JImporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.codec.binary.StringUtils;
import org.bson.Document;
import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.internal.Neo4jConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
	int counter = 0;
	private Connection connect;
	private List<String> queries;

	public Neo4JImportWorker() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("a5");
		collection = database.getCollection("poms");
		gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
				new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class,new DependencyAdapater());
		gson = gsonBuilder.create();
		queries = Collections.synchronizedList(new ArrayList<String>());
	}

	public Stream<Project> getMongoData() {

		Stream<Document> stream = StreamSupport.stream(
				collection.find(new Document()).limit(10000).spliterator(), false);
		return stream.map(document -> {
			counter++;
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(document.toJson()).getAsJsonObject();
			
			if(counter % 100 == 0)
				System.out.println("found " + counter + " new objects");
				
//			System.out.println(jsonObject.get("project"));
			String id = jsonObject.getAsJsonObject("_id").get("$oid").getAsString();
			Project project = gson.fromJson(jsonObject.get("project"),Project.class);
			//TODO nullpointer exception abfangen
			project.setId(id);
			
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

	public void importNeo4J(Stream<Project> stream){
		
		try {
			connect = DriverManager.getConnection("jdbc:neo4j://localhost:7474/", "neo4j", "BIGdata");
			connect.createStatement().executeQuery("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
//			connect.createStatement().executeQuery("CREATE (person1 { personId: 1, started: 1361708546 })");
			
			stream.forEach(project -> {
				try {
					createVersion(project);
					createArtifact(project);
					createGroup(project);
					createArtifactVersionRelation(project);
					createGroupVersionRelation(project);
					if(counter % 100 == 0){
						String query = queries.stream().collect(Collectors.joining("\n WITH 1 as dummy \n"));				
					    connect.createStatement().executeQuery(query);
						queries.clear();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			});
			connect.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createVersion(Project project) throws SQLException{
		String version = project.getVersion();		
		String query = "MERGE(v:version {id:'" + project.id + "'}) "
		+ "ON CREATE SET v.id = '"+project.id+ "'"
		+ "ON CREATE SET v.name = '" + version + "'";
//		connect.createStatement().executeQuery(query);
		queries.add(query);
	}
	
	private void createArtifact(Project project) throws SQLException{
		// TODO Die artifactID ist noch nicht eindeutig

		String query = "MERGE(a:artifact {name:'" + project.artifactId + "'}) "
		+ "ON CREATE SET a.id = '"+project.id+ "'"
		+ "ON CREATE SET a.name = '" + project.artifactId + "'";
		queries.add(query);
//		connect.createStatement().executeQuery(query);
	}
	
	private void createGroup(Project project) throws SQLException{
		String groupid = project.getGroupId();
		
		String query = "MERGE(a:group{name:'" + groupid + "'}) "
				+ "ON CREATE SET a.id = '"+ project.id + "'"
				+ "ON CREATE SET a.name = '" + groupid + "'";
		queries.add(query);
//		connect.createStatement().executeQuery(query);
	}
	
	private void createArtifactVersionRelation(Project project) throws SQLException{
		
		String query = "MATCH(v:version {id:'" + project.id + "'}), (a:artifact {name: '" + project.artifactId + "'}) "
				+ "CREATE (v) -[:gehoert_zu]->(a)";
		queries.add(query);
//		connect.createStatement().executeQuery(query);
	}
	
	private void createGroupVersionRelation(Project project) throws SQLException{
		
		String query = "MATCH(a:artifact {name:'" + project.artifactId + "'}), (g:group {name: '" + project.getGroupId() + "'}) "
				+ "CREATE (a) -[:gehoert_zu]->(g)";
		queries.add(query);
				
//		connect.createStatement().executeQuery(query);
	}
	
	
	public void close() {
		if (mongoClient != null)
			mongoClient.close();
	}

}
