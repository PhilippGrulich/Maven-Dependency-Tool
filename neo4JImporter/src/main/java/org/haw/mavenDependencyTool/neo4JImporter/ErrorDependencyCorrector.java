package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ErrorDependencyCorrector {
	HashMap<String, String> nodes = new HashMap<>(100000000);
	PrintWriter links;
	int errorCounter = 0;
	int correctCounter = 0;

	ErrorDependencyCorrector() throws IOException{
		System.out.println("---------------Fixing dependency errors---------------------");
		Files.lines(Paths.get("data/data.csv")).forEach(nodeString->{
			String key = nodeString.split(";")[0];
			if(!key.startsWith("Version"))
				return;
		
			int lastSub = 0;
			for(int i = 0; i< key.toCharArray().length;i++){
				if(key.toCharArray()[i]=='_')
					lastSub=i;
			}
			
			key = key.substring(0, lastSub+1);
			String version = nodeString.split(";")[1];					
			if(nodes.containsKey(key)){
				String oldVersion = nodes.get(key);
				if(oldVersion.compareTo(version)>1){
					nodes.put(key,version);
				}
			}else{
				nodes.put(key,version);
			}
		});
		links = new PrintWriter(new BufferedWriter(new FileWriter("data/links.csv", true)));
		Files.lines(Paths.get("error.csv")).forEach(this::fixError);
		links.flush();
		System.out.println("Correct Dependenies" + correctCounter+" ---- Unknown Dependencies"+errorCounter);
	}

	public static void main(String[] args) throws IOException {
		new ErrorDependencyCorrector();

	}

	private void fixError(String error) {

		String key = error.split(";")[0];
		String tempVersionKey = error.split(";")[1];

		if (nodes.containsKey(tempVersionKey)) {
			String first = nodes.get(tempVersionKey);
			if (first != null) {
				links.println(String.format("%s;%s;%s", key, tempVersionKey + first, "nutzt"));
				correctCounter++;
				return;
			}
		}
		System.err.println(error);
		errorCounter++;
	}

}
