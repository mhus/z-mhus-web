/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.api;

import java.util.LinkedList;
import java.util.List;

public class TypeDefinition {

    private String name;
    private String[] refs;
    private LinkedList<TypeHeader> headers = new LinkedList<>();
    private String mimeType;

    public void setReferences(String refs) {
        this.refs = null;
        if (refs == null) return;
        this.refs = refs.split(",");
    }

    public void setExtends(String[] refs) {
        this.refs = refs;
    }

    public String[] getExtends() {
        return refs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addHeader(TypeHeader header) {
        headers.add(header);
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<TypeHeader> getHeaders() {
        return headers;
    }
}
