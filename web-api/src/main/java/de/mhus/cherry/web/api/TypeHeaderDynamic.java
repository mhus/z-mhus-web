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

    public TypeHeaderDynamic(String key, String value) {
        this.key = key;
        this.definition = value;
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
        if (value instanceof Long)
            resp.addDateHeader(key, (Long)value);
        else
        if (value instanceof Integer)
            resp.setIntHeader(key, (Integer)value);
        else
            resp.setHeader(key, value.toString());
    }

    private void updateValue() {
        if (definition == null) return;
        if (!MPeriod.isTimeOut(lastUpdate, timeout)) return;
        lastUpdate = System.currentTimeMillis();
        if (definition.equals("$now"))
            value = System.currentTimeMillis();
        else
        if (definition.startsWith("$in "))
            value = (new MPeriod(definition.substring(4)).getAllMilliseconds() + System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return key + "=" + value + "," + definition;
    }
    
    public static class Factory implements TypeHeaderFactory {

        @Override
        public TypeHeader create(IConfig header) throws MException {
            String key = header.getString("key", null);
            String value = header.getString("definition",null);
            if (key == null || value == null) return null;
            return new TypeHeaderDynamic(key, value);
        }

    }
}
