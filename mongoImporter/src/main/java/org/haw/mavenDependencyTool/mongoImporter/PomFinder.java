package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class PomFinder   {
	String rootDir;
	public PomFinder(String rootDir) {
		this.rootDir=rootDir;
	}
	public Stream<Path> search() throws IOException {
		Path path = Paths.get(rootDir);
		return Files.walk(path, 20).parallel()
		.filter(p -> 		
		p.toString().endsWith(".pom"));		
	}	
}
