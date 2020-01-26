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

import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class TypeHeaderDynamic implements TypeHeader {

    private String key;
    private Object value;
    private String definition;
    private long lastUpdate;
    private long timeout = MPeriod.MINUTE_IN_MILLISECOUNDS * 15;
    private boolean add;

    public TypeHeaderDynamic(String key, String value, boolean add) {
        this.key = key;
        this.definition = value;
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

    public String getDefinition() {
        return definition;
    }

    @Override
    public void appendTo(HttpServletResponse resp) {
        updateValue();
        if (value == null) return;
        if (value instanceof Long) {
            if (add) resp.addDateHeader(key, (Long) value);
            else resp.setDateHeader(key, (Long) value);
        } else if (value instanceof Integer) {
            if (add) resp.addIntHeader(key, (Integer) value);
            else resp.setIntHeader(key, (Integer) value);
        } else {
            if (add) resp.addHeader(key, value.toString());
            else resp.setHeader(key, value.toString());
        }
    }

    private void updateValue() {
        if (definition == null) return;
        if (!MPeriod.isTimeOut(lastUpdate, timeout)) return;
        lastUpdate = System.currentTimeMillis();
        if (definition.equals("$now")) value = System.currentTimeMillis();
        else if (definition.startsWith("$in "))
            value =
                    (new MPeriod(definition.substring(4)).getAllMilliseconds()
                            + System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return key + "=" + value + "," + definition;
    }

    public static class Factory implements TypeHeaderFactory {

        @Override
        public TypeHeader create(IConfig header) throws MException {
            String key = header.getString("key", null);
            String value = header.getString("definition", null);
            boolean add = header.getBoolean("add", false);
            if (key == null || value == null) return null;
            return new TypeHeaderDynamic(key, value, add);
        }
    }

    @Override
    public boolean addHeaderLine() {
        return add;
    }
}
