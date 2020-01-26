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

import javax.servlet.http.HttpServletResponse;

public class TypeHeaderSimple implements TypeHeader {

    private String key;
    private String value;
    private boolean add;

    public TypeHeaderSimple(String key, String value, boolean add) {
        this.key = key;
        this.value = value;
        this.add = add;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void appendTo(HttpServletResponse resp) {
        if (add) resp.addHeader(key, value);
        else resp.setHeader(key, value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public boolean addHeaderLine() {
        return add;
    }
}
