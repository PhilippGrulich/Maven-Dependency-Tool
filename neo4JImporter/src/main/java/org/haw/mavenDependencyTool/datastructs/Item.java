package org.haw.mavenDependencyTool.datastructs;

import com.google.gson.JsonElement;

public class Item {
	private String artifactId;
	JsonElement version;
	private String groupId;

	public Item(String artifactId, JsonElement version, String groupId) {
		super();
		this.setArtifactId(artifactId);
		this.version = version;
		this.setGroupId(groupId);
	}

	@Override
	public String toString() {
		return "Item [artifactId=" + getArtifactId() + ", version=" + version
				+ ", groupId=" + getGroupId() + "]";
	}

	public String getVersion() {
		if (version != null && version.isJsonPrimitive()) {
			String versionString = version.getAsString();
			return versionString;
		} else if (version != null) {
			if (version.getAsJsonObject().has("content")) {
				return version.getAsJsonObject().get("content").getAsString();
			} else
				return version.getAsJsonObject().get("$numberLong")
						.getAsString();
		}

		return null;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

}
