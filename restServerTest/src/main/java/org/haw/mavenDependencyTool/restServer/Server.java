package org.haw.mavenDependencyTool.restServer;

import static spark.Spark.*;

import org.haw.mavenDependencyTool.restServer.routes.Project;
import org.haw.mavenDependencyTool.restServer.routes.SearchRoute;
import org.haw.mavenDependencyTool.restServer.util.JsonTransformer;
import org.haw.mavenDependencyTool.restServer.util.MongoDb;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Hello world!
 *
 */
public class Server {
	public static void main(String[] args) {

		MongoDb mongo = new MongoDb();
		
		JsonTransformer json = new JsonTransformer();

		staticFileLocation("public");

		new SearchRoute(json, mongo);
	}
}
