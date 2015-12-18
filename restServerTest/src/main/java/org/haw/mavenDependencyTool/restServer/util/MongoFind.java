package org.haw.mavenDependencyTool.restServer.util;
import static com.mongodb.client.model.Projections.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.TextSearchOptions;
import com.mongodb.util.JSON;

public class MongoFind implements Iterable<MongoObject>  {
	
	FindIterable<Document> findIterable;
	public MongoFind(FindIterable<Document> findIterable) {
		this.findIterable = findIterable;
		
	}
	
	public MongoFind projection(String projection){
		DBObject dbObject = (DBObject) JSON.parse(projection);			
		return new MongoFind(findIterable.projection((Bson) dbObject));
	}
	
	public MongoFind sort(String sort){
		DBObject dbObject = (DBObject) JSON.parse(sort);	
		return new MongoFind(findIterable.sort((Bson) dbObject));
	}
	
	public MongoFind limit(int i){
		return new MongoFind(findIterable.limit(i));
	}

	@Override
	public Iterator<MongoObject> iterator() {
		return new Iterator<MongoObject>(){
			Iterator<Document> i = findIterable.iterator();
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public MongoObject next() {
				JsonParser parser = new JsonParser();		
				JsonObject o = parser.parse(i.next().toJson()).getAsJsonObject();
				return new MongoObject(o);
			
			}
			
		};
		
	}
	
	
	public Stream<MongoObject> asStream(){
		return StreamSupport.stream(this.spliterator(),false);
	}

	public Stream<MongoObject> asStreamParallel(){
		return StreamSupport.stream(this.spliterator(),true);
	}


	

}
