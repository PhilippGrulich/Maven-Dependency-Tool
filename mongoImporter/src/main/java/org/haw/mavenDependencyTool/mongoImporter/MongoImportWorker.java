package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;
import org.json.XML;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoImportWorker {
//	MongoClient mongoClient = new MongoClient("localhost", 27017);
//	MongoDatabase database = mongoClient.getDatabase("a5");
//	MongoCollection<Document> collection = database.getCollection("poms");
	List<Document> documentList = new ArrayList<>();
	int counter = 0;
	
	MongoImportWorker(Stream<String> stream){
		stream.map(XML::toJSONObject).map(json->json.toString()).forEach(this::importInMongoDB);
		//mongoClient.close();
	}
	
	private void importInMongoDB(String pomContent){
		//System.out.println(pomContent);
		DBObject dbObject = (DBObject) JSON.parse(pomContent);
		addDocument(new Document(dbObject.toMap()));
	}
	
	public synchronized void addDocument(Document doc){
		documentList.add(doc);
		if(documentList.size()==500){
		
			transverToMongodb();
			documentList.clear();
			
		}
	}
	
	public void transverToMongodb(){
		System.out.println(++counter + ". Import the next "+documentList.size()+" elements");
		//collection.insertMany(documentList);
	
	}
}
