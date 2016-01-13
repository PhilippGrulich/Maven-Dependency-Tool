package org.haw.mavenDependencyTool.gitHubPomFinder;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;

public class App {
	static int lastID = 0;
	private static GitHub github;
	private static Path path;

	public static void main(String[] args) throws IOException {

		 path = Paths.get("output.txt");
		try {
			Files.createFile(path);
		} catch (FileAlreadyExistsException e) {
			System.err.println("already exists: " + e.getMessage());
			
			Files.lines(path).forEach(x-> {
				try{
					int id = Integer.valueOf(x.split(";")[0]);
					if(id>lastID){
						lastID = id;
					}
				}catch (Exception e2) {
					// TODO: handle exception
				}
				
				
				
			});
		}

		 github = GitHubBuilder.fromCredentials()
				.withRateLimitHandler(RateLimitHandler.WAIT).build();
		
	
		// Alle 10 Minuten eine Log Ausgabe
		new Thread(  () -> { 
			while(true){				
				try {
					System.err.println(github.getRateLimit());
					System.err.println("LastID "+ lastID);

					Thread.sleep(600000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}		
		
		}).start();

		
	
	}

	public static void getAll(){
		System.out.println("Start Download at "+ lastID);
		try{
			github.listAllPublicRepositories(String.valueOf(lastID)).forEach(
					x -> {
						String message = String.format("%s;%s;\n", x.getId(),
								x.getName());
						lastID = x.getId();
						try {
							Files.write(path, message.getBytes(),
									StandardOpenOption.APPEND);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});			
		}catch(Exception e){
			e.printStackTrace();
			getAll();
		}
		
	}
}
