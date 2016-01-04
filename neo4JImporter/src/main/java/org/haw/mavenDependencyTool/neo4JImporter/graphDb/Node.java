package org.haw.mavenDependencyTool.neo4JImporter.graphDb;

public class Node {
	
	int keyValue;
	
	

	public Node(int keyValue) {
		super();
		this.keyValue = keyValue;
	}

	public int getId() {
		return keyValue;
	}

	void setId(int keyValue) {
		this.keyValue = keyValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + keyValue;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (keyValue != other.keyValue)
			return false;
		return true;
	}	
}
