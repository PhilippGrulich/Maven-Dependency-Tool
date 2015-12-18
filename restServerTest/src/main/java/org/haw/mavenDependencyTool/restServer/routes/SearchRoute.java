package org.haw.mavenDependencyTool.restServer.routes;

import java.util.List;
import java.util.stream.Collectors;

import org.haw.mavenDependencyTool.restServer.util.JsonTransformer;
import org.haw.mavenDependencyTool.restServer.util.MongoDb;
import org.haw.mavenDependencyTool.restServer.util.MongoObject;

public class SearchRoute extends RestRoute{

	public SearchRoute(JsonTransformer json, MongoDb mongo) {
		super(json, mongo);
	
		getJSON("/search", (request,response)->{
			String query = request.queryParams("q");  
			
			 List<MongoObject> res = mongo
					 .query("{$text: { $search: '"+query+"' }}")
					 .projection("{ 'project.artifactId':1, score: { $meta: 'textScore' } }")
					 .sort("{ score: { $meta: 'textScore' } }")
					 .limit(10)
					 .asStreamParallel()
					 .map(x-> x.subDoc("project","score"))
					 .collect(Collectors.toList());
				
			return res ;
		});		
	
	}
}
