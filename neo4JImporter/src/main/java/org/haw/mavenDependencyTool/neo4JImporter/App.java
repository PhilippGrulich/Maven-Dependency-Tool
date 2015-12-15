package org.haw.mavenDependencyTool.neo4JImporter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Neo4JImportWorker neo4JImporter = new Neo4JImportWorker();
    	for(String item : neo4JImporter.getMongoData()){
    		System.out.println(item);
    	}
    	
    	neo4JImporter.close();
    	
    }
}
