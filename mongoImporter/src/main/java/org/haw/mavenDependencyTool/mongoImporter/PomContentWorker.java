package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
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
			Stream<String> stream = Files.lines(p,Charset.forName("UTF-8"));
			stream.forEach(line->sb.append(line));
			stream.close();
		}catch (Exception e) {
			System.out.println(p.toAbsolutePath().toString()+" MalformedInputException ");
		}				
		return sb.toString();
	}
}
