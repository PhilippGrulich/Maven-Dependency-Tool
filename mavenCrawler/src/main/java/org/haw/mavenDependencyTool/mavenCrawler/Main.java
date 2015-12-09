package org.haw.mavenDependencyTool.mavenCrawler;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * Hello world!
 *
 */
public class Main 
{
	
	public static String RemoteDir = "http://central.maven.org/maven2/";
	public static String SaveToDir = "C:\\Users\\Philipp\\Documents\\test\\data\\";
	public static String TempDataDir = "C:\\Users\\Philipp\\Documents\\test\\storrage";
    public static void main( String[] args ) throws Exception
    {
    	
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(TempDataDir);
        config.setUserAgentString("Mozilla/1.0N (Windows)");
        config.setResumableCrawling(true);
        
        /*
         * crawler4j is designed very efficiently and has the ability to crawl domains very fast  (e.g., it has been able to crawl 200 Wikipedia pages per second).
         * However, since this is against crawling policies and puts huge load on servers (and they might block you!),
         * by default crawler4j waits at least 200 milliseconds between requests. However, this parameter can be tuned:
         */
        config.setPolitenessDelay(200);
        	
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);        
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);        
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(RemoteDir);
        
    
        
        Crawler.configure(SaveToDir);
        
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(Crawler.class, numberOfCrawlers);
    }
}
