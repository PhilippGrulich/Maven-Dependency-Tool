package org.haw.mavenDependencyTool.neo4JImporter;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JsonSyntaxException, IOException
    {
    	//Neo4JImportWorker neo4JImporter = new Neo4JImportWorker();

 	//neo4JImporter.getMongoData();
    	
   	new Neo4JImportCSV().importNeo4J(new Neo4JImportFileWorker().getMongoData());
   	new ErrorDependencyCorrector();
    //neo4JImporter.getMongoData().forEach(p-> {System.out.println("test");});
   	//neo4JImporter.close();
    	
   
   	}
}
