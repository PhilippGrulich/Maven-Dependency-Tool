package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DependencyAdapater extends TypeAdapter<Dependencies> {
	

	public Dependencies read(JsonReader reader) throws IOException {

		
		GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
				new ArrayAdapterFactory());
		gsonBuilder.registerTypeAdapter(Dependencies.class,new DependencyAdapater());
		
		Gson gson = gsonBuilder.create();
		if (reader.peek() == JsonToken.BEGIN_OBJECT) {
			Dependencies inning = gson.fromJson(reader, Dependencies.class);
			return inning;
		}
//		} else if (reader.peek() == JsonToken.STRING) {
//
//			return new Dependencies(null);
//
//		}
		return new Dependencies(new ArrayList<Item>());
	}

	@Override
	public void write(JsonWriter arg0, Dependencies arg1) throws IOException {
		// TODO Auto-generated method stub

	}
}