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

    	neo4JImporter.getMongoData().forEach(project -> System.out.println(project));
    	
    	neo4JImporter.close();
    	
    }
}
