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

//    	neo4JImporter.getMongoData();
    	
//    	neo4JImporter.importNeo4J(neo4JImporter.getMongoData());
    neo4JImporter.getMongoData().forEach(p-> {int i = 0;});
    	neo4JImporter.close();
    	
    }
}
