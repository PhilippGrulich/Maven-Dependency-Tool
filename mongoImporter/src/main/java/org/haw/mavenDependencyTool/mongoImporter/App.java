package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class App {
	public static void main(String[] args) {
		
		try {
			PomFinder p = new PomFinder(args[0]);
			Stream<Path> pomPathStream = p.search();
			Stream<String> pomContentStream = new PomContentWorker(pomPathStream).getStream();
			MongoImportWorker mongo = new MongoImportWorker(pomContentStream);
			mongo.transverToMongodb();
			mongo.mongoClient.close();
			System.out.println("Finish");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
