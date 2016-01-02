/**
 * Created by Philipp on 21.12.2015.
 */
"use strict";

class DataService{
    constructor() {
       this.neo4j = new Neo4jService();
    }

    query(query){
        var statement = new Neo4jQuery(query);
        return this.neo4j.query(statement);
    }
}