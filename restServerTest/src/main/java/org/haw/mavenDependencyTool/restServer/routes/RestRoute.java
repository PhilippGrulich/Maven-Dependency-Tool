package org.haw.mavenDependencyTool.restServer.routes;


import org.haw.mavenDependencyTool.restServer.util.JsonTransformer;
import org.haw.mavenDependencyTool.restServer.util.MongoDb;
import org.jongo.Jongo;

import spark.Route;
import spark.Spark;

public abstract class RestRoute {
	JsonTransformer json;
	MongoDb mongo;

	RestRoute(JsonTransformer json, MongoDb mongo) {
		this.json = json;
		this.mongo = mongo;

	}

	public void get(String path, Route route) {
		Spark.get(path, route);
	}

	public void getJSON(String path, final Route route) {
		Spark.get(path, route, json);
	}

	
}
