package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class App {
	public static void main(String[] args) {
		try {
			PomFinder p = new PomFinder("C://Users//Philipp//.m2//");
			Stream<Path> pomPathStream = p.search();
			Stream<String> pomContentStream = new PomContentWorker(pomPathStream).getStream();
			new MongoImportWorker(pomContentStream).transverToMongodb();
			
			System.out.println("Finish");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
