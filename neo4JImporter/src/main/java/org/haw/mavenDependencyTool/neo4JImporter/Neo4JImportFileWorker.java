package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Neo4JImportFileWorker {

	
	GsonBuilder gsonBuilder;
	Gson gson;
	int counter = 0;
	
	private long startDate = System.currentTimeMillis();

	public Neo4JImportFileWorker() {
		
		gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class, new DependencyAdapater());
		gson = gsonBuilder.create();
		
	}

	public Stream<Project> getMongoData() throws JsonSyntaxException, IOException {
		
		return Files.lines( Paths.get("poms.json")).map(document -> {

			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(document).getAsJsonObject();
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
					System.out.println("Exeption:" +document);
					e.printStackTrace();
			}
			return null;

		});

	}

}
