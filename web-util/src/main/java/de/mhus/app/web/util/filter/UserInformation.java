/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.web.util.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
        for (JsonNode grp : ((ArrayNode) json.get("groups"))) {
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
