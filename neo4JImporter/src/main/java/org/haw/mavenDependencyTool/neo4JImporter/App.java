package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

public class App {
	public static void main(String[] args) throws JsonSyntaxException,
			IOException {

		new Neo4JImportCSV().importNeo4J(new Neo4JImportFileWorker()
				.getMongoData());
		new ErrorDependencyCorrector();

	}
}
