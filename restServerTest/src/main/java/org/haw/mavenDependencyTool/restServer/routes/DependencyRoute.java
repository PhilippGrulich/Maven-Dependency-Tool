package org.haw.mavenDependencyTool.restServer.routes;

import java.util.List;
import java.util.stream.Collectors;

import org.haw.mavenDependencyTool.restServer.util.Cache;
import org.haw.mavenDependencyTool.restServer.util.JsonTransformer;
import org.haw.mavenDependencyTool.restServer.util.MongoDb;
import org.haw.mavenDependencyTool.restServer.util.MongoObject;
import org.haw.mavenDependencyTool.restServer.util.Neo4JHttpProxy;

public class DependencyRoute extends RestRoute{

	public DependencyRoute(JsonTransformer json, MongoDb mongo) {
		super(json, mongo);
		Neo4JHttpProxy neo = new Neo4JHttpProxy();
		Cache cache = new Cache();
	
		get("/dependency/:artifactID", (request,response)->{
			String artifactID = request.params(":artifactID");
			String result = cache.cache("dependency_"+artifactID, ()->{
				return neo.runQuery("MATCH (a{name:'+artifactID+'}) -[r:gehoert_zu]-> (b) RETURN r LIMIT 100;");
			});		
			response.header("Content-Type", "application/json");
			return result ;
		});		
		
		get("/dependency/", (request,response)->{
			String artifactID = request.params(":artifactID");
			String result = cache.cache("dependency_"+artifactID, ()->{
				return neo.runQuery("MATCH a -[r:gehoert_zu]-> (b) RETURN r LIMIT 100;");
			});		
			response.header("Content-Type", "application/json");
			return result ;
		});		
	}
}
