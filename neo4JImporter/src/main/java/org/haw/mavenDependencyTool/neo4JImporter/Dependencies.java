package org.haw.mavenDependencyTool.neo4JImporter;

import java.util.List;

public class Dependencies {
	List<Item> dependency;

	public Dependencies(List<Item> dependency) {
		super();
		this.dependency = dependency;
	}

	@Override
	public String toString() {
		return "Dependencies [dependency=" + dependency.toString() + "]";
	}
	
	
}
