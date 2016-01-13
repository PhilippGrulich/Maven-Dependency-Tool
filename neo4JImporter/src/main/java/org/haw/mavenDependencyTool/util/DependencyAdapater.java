package org.haw.mavenDependencyTool.util;

import java.io.IOException;
import java.util.ArrayList;

import org.haw.mavenDependencyTool.datastructs.Dependencies;
import org.haw.mavenDependencyTool.datastructs.Item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DependencyAdapater extends TypeAdapter<Dependencies> {

	public Dependencies read(JsonReader reader) throws IOException {

		GsonBuilder gsonBuilder = new GsonBuilder()
				.registerTypeAdapterFactory(new ArrayAdapterFactory());

		Gson gson = gsonBuilder.create();
		if (reader.peek() == JsonToken.BEGIN_OBJECT) {
			Dependencies inning = gson.fromJson(reader, Dependencies.class);
			return inning;
		} else if (reader.peek() == JsonToken.STRING) {

			return new Dependencies(new ArrayList<Item>());

		} else if (reader.peek() == JsonToken.NAME)
			System.out.println("found name");

		return new Dependencies(new ArrayList<Item>());
	}

	@Override
	public void write(JsonWriter arg0, Dependencies arg1) throws IOException {

	}
}