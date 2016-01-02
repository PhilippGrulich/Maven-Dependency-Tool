package org.haw.mavenDependencyTool.restServer.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class Neo4JHttpProxy {
	GenericUrl url = new GenericUrl("http://localhost:7474/db/data/transaction/commit");
	
	String queryTemplate = " {\"statements\":["
			+ " {\"statement\":\"%s\" ,"
			+ " \"resultDataContents\":[\"graph\"],"
			+ "\"includeStats\":true} "
			+ "]} "
			+ "] }";
	
	
	public String runQuery(String query) throws IOException{
		  	String fullQuery = String.format(queryTemplate, query);
		  	System.out.println(fullQuery);
		  	InputStreamContent isc= new InputStreamContent("application/json", new ByteArrayInputStream(fullQuery.getBytes(StandardCharsets.UTF_8)));
	        HttpRequest request = new ApacheHttpTransport().createRequestFactory().buildPostRequest(url, isc);			
	        HttpHeaders headers = new HttpHeaders();
	        headers.setAuthorization("Basic bmVvNGo6QklHZGF0YQ==");
	        headers.setContentType("application/json");
	        request.setHeaders(headers);
	        HttpResponse response = request.execute();
	        InputStream is = response.getContent();
	        String lines = "";
	        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
	        	lines = buffer.lines().collect(Collectors.joining("\n"));
	        }	       
	        response.disconnect();
	        return lines;
		
	}
	
}
