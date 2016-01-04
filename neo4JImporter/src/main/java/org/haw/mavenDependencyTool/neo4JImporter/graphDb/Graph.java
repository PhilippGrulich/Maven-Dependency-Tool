package org.haw.mavenDependencyTool.neo4JImporter.graphDb;

import java.util.HashMap;
import java.util.HashSet;

public class Graph {
	int nextID = 0;
	HashSet<Node> nodes = new HashSet<Node>();
	HashMap<Node, HashSet<Node>> links = new HashMap<Node, HashSet<Node>>();
	
	public void addNode(Node node){
		nodes.add(node);
	}
		
	public void addLink(Node node1, Node node2){
		HashSet<Node> oldLiks = links.get(node1);
		if(oldLiks == null){			
			oldLiks=  new HashSet<Node>();			
		}
		oldLiks.add(node2);
	}
	
		
}
