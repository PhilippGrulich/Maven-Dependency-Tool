package org.haw.mavenDependencyTool.restServer.routes;

public class Project {
	String artifactId;
	String score;



	public Project(String artifactId, String score) {
		super();
		this.artifactId = artifactId;
		this.score = score;
	}



	@Override
	public String toString() {
		return "Project [artifactId=" + artifactId + ", score=" + score + "]";
	}


}
