package org.haw.mavenDependencyTool.mavenCrawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
	private static int count = 0;

	private static Path storageFolder;

	public static void configure(String storageFolderName) {

		storageFolder = Paths.get(storageFolderName);
		if (!storageFolder.toFile().exists()) {
			storageFolder.toFile().mkdirs();
		}
	}

	/**
	 * This method receives two parameters. The first parameter is the page in
	 * which we have discovered this new url and the second parameter is the new
	 * url. You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic). In this example,
	 * we are instructing the crawler to ignore urls that have css, js, git, ...
	 * extensions and to only accept urls that start with
	 * "http://www.ics.uci.edu/". In this case, we didn't need the referringPage
	 * parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL();			
		return href.startsWith(Main.RemoteDir) && (href.endsWith("/")||href.endsWith(".pom"));
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();		
		if(count++%1000==0){
			System.out.println(count + ": Aktuelle URL "+ url);
		}	
		if(!url.endsWith(".pom"))
			return;
					
		String filename = storageFolder.toString() + "\\" + url.replaceFirst("http://", "").replaceAll("/", "_");
		
		try {
			Files.write(Paths.get(filename), page.getContentData());
			System.out.println("Found pom "+ filename);
		} catch (IOException iox) {
			iox.printStackTrace();
			System.out.println("Failed to write file: " + filename);
		}		
	}
}