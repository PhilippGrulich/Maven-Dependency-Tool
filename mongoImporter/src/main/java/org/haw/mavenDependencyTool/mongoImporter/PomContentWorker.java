package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class PomContentWorker {
	Stream<String> pomContentStream;
	PomContentWorker(Stream<Path> stream){
		pomContentStream = stream.map(this::loadFile);
	}
	
	public Stream<String> getStream() {
		return pomContentStream;
	}
	
	
	private String loadFile(Path p){
		StringBuilder sb = new StringBuilder();
		try {
			Files.lines(p).forEach(line->sb.append(line));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
}
