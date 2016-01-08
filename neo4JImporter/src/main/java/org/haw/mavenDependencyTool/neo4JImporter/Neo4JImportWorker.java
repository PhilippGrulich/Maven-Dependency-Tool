package org.haw.mavenDependencyTool.neo4JImporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
	private long startDate = System.currentTimeMillis();

	public Neo4JImportWorker() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("a5");
		collection = database.getCollection("poms");
		gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class, new DependencyAdapater());
		gson = gsonBuilder.create();
		queries = Collections.synchronizedList(new ArrayList<String>());
	}

	public Stream<Project> getMongoData() {
		
//		Document query = new Document();
//		query.append("project.artifactId", "notification-amazon").append("project.version", "3.6.0");
		DBObject clause1 = new BasicDBObject("project.artifactId", "notification-amazon");  
		DBObject clause2 = new BasicDBObject("project.artifactId", "aws-java-sdk");    
		DBObject clause3 = new BasicDBObject("project.artifactId", "commons-validator");    
		BasicDBList or = new BasicDBList();
		or.add(clause1);
		or.add(clause2);
		or.add(clause3);
		DBObject query2 = new BasicDBObject("$or", or);
		Document query = new Document();
		Stream<Document> stream = StreamSupport.stream(collection.find(query).spliterator(), true);
		return stream.map(document -> {

			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(document.toJson()).getAsJsonObject();
			try {

				// System.out.println(jsonObject.get("project"));
				String id = jsonObject.getAsJsonObject("_id").get("$oid").getAsString();
				Project project = gson.fromJson(jsonObject.get("project"), Project.class);
				// TODO nullpointer exception abfangen
				if (project == null)
					System.out.println("project null: " + jsonObject);
				else
					project.setId(id);
				
				//System.out.println(counter++);

				// System.out.println(project);
				return project;
			} catch (Exception e) {
					System.out.println("Exeption:" +document.toJson());
					e.printStackTrace();
			}
			return null;

		});

	}

	public void importNeo4J(Stream<Project> stream) {

		try {
			connect = DriverManager.getConnection("jdbc:neo4j://localhost:7474/", "neo4j", "BIGdata");
			connect.createStatement().executeQuery("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");

			stream.forEach(project -> {
				counter++;
				if (counter % 100 == 0) {
					System.out.println("found " + counter + " new objects");
					System.out.println(System.currentTimeMillis() - startDate);
					startDate = System.currentTimeMillis();
				}
				try {
					createVersion(project);
					createArtifact(project);
					createGroup(project);
					createArtifactVersionRelation(project);
					createGroupVersionRelation(project);
					if (counter % 100 == 0) {
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

	private void createVersion(Project project) throws SQLException {
		String version = project.getVersion();

		String query = "MERGE(v:version {id:'" + project.id + "'}) " + "ON CREATE SET v.id = '" + project.id + "'"
				+ "ON CREATE SET v.name = '" + version + "'";
		// connect.createStatement().executeQuery(query);
		queries.add(query);
	}

	private void createArtifact(Project project) throws SQLException {
		// Die artifactID ist eindeutig über den key
		String key = project.getGroupId() + "_" + project.artifactId;
		String query = "MERGE(a:artifact {key:'" + key + "'}) " + "ON CREATE SET a.id = '" + project.id + "'"
				+ " ON CREATE SET a.name = '" + project.artifactId + "'" + " ON CREATE SET a.key = '" + key + "'";
		queries.add(query);
		// connect.createStatement().executeQuery(query);
	}

	private void createGroup(Project project) throws SQLException {
		String groupid = project.getGroupId();

		String query = "MERGE(a:group{name:'" + groupid + "'}) " + "ON CREATE SET a.id = '" + project.id + "'"
				+ "ON CREATE SET a.name = '" + groupid + "'";
		queries.add(query);
		// connect.createStatement().executeQuery(query);
	}

	private void createArtifactVersionRelation(Project project) throws SQLException {
		String artifactKey = project.getGroupId() + "_" + project.artifactId;
		String query = "MATCH(v:version {id:'" + project.id + "'}), (a:artifact {key: '" + artifactKey + "'}) "
				+ "CREATE UNIQUE (v) -[:gehoert_zu]->(a)";
		queries.add(query);
		// connect.createStatement().executeQuery(query);
	}

	private void createGroupVersionRelation(Project project) throws SQLException {
		String artifactKey = project.getGroupId() + "_" + project.artifactId;
		String query = "MATCH(a:artifact {key:'" + artifactKey + "'}), (g:group {name: '" + project.getGroupId()
				+ "'}) " + "CREATE UNIQUE (a) -[:gehoert_zu]->(g)";
		queries.add(query);

		// connect.createStatement().executeQuery(query);
	}

	public void close() {
		if (mongoClient != null)
			mongoClient.close();
	}

}
