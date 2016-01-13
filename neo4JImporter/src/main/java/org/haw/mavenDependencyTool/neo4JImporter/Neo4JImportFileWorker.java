package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.haw.mavenDependencyTool.datastructs.Dependencies;
import org.haw.mavenDependencyTool.datastructs.Project;
import org.haw.mavenDependencyTool.util.ArrayAdapterFactory;
import org.haw.mavenDependencyTool.util.DependencyAdapater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Neo4JImportFileWorker {

	GsonBuilder gsonBuilder;
	Gson gson;
	int counter = 0;

	public Neo4JImportFileWorker() {

		gsonBuilder = new GsonBuilder()
				.registerTypeAdapterFactory(new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class,
				new DependencyAdapater());
		gson = gsonBuilder.create();

	}

	public Stream<Project> getMongoData() throws JsonSyntaxException,
			IOException {

		return Files.lines(Paths.get("/tmp/poms.json")).map(
				document -> {

					JsonParser parser = new JsonParser();
					JsonObject jsonObject = parser.parse(document)
							.getAsJsonObject();
					try {

						String id = jsonObject.getAsJsonObject("_id")
								.get("$oid").getAsString();
						Project project = gson.fromJson(
								jsonObject.get("project"), Project.class);
						if (project == null)
							System.out.println("project null: " + jsonObject);
						else
							project.setId(id);
						return project;
					} catch (Exception e) {
						System.out.println("Exeption:" + document);
						e.printStackTrace();
					}
					return null;

				});

	}

}
