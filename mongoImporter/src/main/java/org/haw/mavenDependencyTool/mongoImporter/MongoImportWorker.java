package org.haw.mavenDependencyTool.mongoImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoImportWorker {
	MongoClient mongoClient = new MongoClient("localhost", 27017);
	MongoDatabase database = mongoClient.getDatabase("a5");
	MongoCollection<Document> collection = database.getCollection("poms");
	List<Document> documentList = new ArrayList<>();
	int counter = 0;

	MongoImportWorker(Stream<String> stream) {
		stream.filter(s->!s.isEmpty()).map(this::xmlToJSON).filter(json->json!=null).map(this::json)
				.map(json -> json.toString()).forEach(this::importInMongoDB);
		
	}
	
	private JSONObject xmlToJSON(String json){
		try{
			return XML.toJSONObject(json);
		}catch (Exception e){
			System.out.println(json);
			System.out.println(e.getMessage());
		}
		return null;
		
	}

	private JSONObject json(JSONObject json) {

		List<String> keyList = new ArrayList<String>();

		json.keys().forEachRemaining(key -> keyList.add((String) key));

		// json.keys().forEachRemaining(key -> System.out.println(key));
		for (String keyString : keyList) {

			JSONArray array = json.optJSONArray(keyString);
			JSONObject obj = json.optJSONObject(keyString);

			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject arrayObjs = array.optJSONObject(i);
					if (arrayObjs != null) {
						json(arrayObjs);
					}
					
					if (keyString.contains(".")) {
						json.remove(keyString);
						json.put(keyString.replaceAll("\\.", "_"), arrayObjs);
					}
				}
			} else if (obj != null) {
				JSONObject newJSONOBject = json(obj);
				if (keyString.contains(".")) {
					json.remove(keyString);
					json.put(keyString.replaceAll("\\.", "_"), newJSONOBject);
				}
			} else {
				if (keyString.contains(".")) {
					Object value = json.remove(keyString);
					json.put(keyString.replaceAll("\\.", "_"), value);
				}
			}

		}
		// System.out.println("*");
		// json.keys().forEachRemaining(key -> System.out.println(key));
		// System.out.println("************************");
		return json;
	}

	private void importInMongoDB(String pomContent) {
		//System.out.println(pomContent);
		DBObject dbObject = (DBObject) JSON.parse(pomContent);
		addDocument(new Document(dbObject.toMap()));
		 //collection.insertOne(new Document(dbObject.toMap()));
	}

	public synchronized void addDocument(Document doc) {
		documentList.add(doc);
		if (documentList.size() == 500) {

			transverToMongodb();
			documentList.clear();

		}
	}

	public void transverToMongodb() {
		System.out.println(++counter + ". Import the next "
				+ documentList.size() + " elements");
		if (!documentList.isEmpty())
			collection.insertMany(documentList);

	}
}
