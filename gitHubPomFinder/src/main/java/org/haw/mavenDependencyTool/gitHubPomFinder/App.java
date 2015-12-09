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
	public static void main(String[] args) throws IOException {

		Path path = Paths.get("output.txt");
		try {
			Files.createFile(path);
		} catch (FileAlreadyExistsException e) {
			System.err.println("already exists: " + e.getMessage());
		}

		GitHub github = GitHubBuilder.fromCredentials()
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

		github.listAllPublicRepositories().forEach(
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
		
	}
}
