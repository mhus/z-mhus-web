package de.mhus.cherry.web.util.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class UserInformation {

	private String userName;
	private String displayName;
	private HashSet<String> groups;
	private String ticket;
	private Set<String> groups_;

	public UserInformation(JsonNode json, String ticket) {
		this.ticket = ticket;
		userName = json.get("name").asText();
		displayName = json.get("displayname").asText();
		groups = new HashSet<>();
		groups_ = Collections.unmodifiableSet(groups);
		for ( JsonNode grp : ((ArrayNode)json.get("groups"))) {
			groups.add(grp.asText());
		}
	}

	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean hasGroup(String name) {
		return groups.contains(name);
	}
	
	public Set<String> getGroups() {
		return groups_;
	}
	
	public String getTicket() {
		return ticket;
	}
	
}
