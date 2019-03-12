package de.mhus.cherry.web.util.webspace;

import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.core.MPeriod;

public class TypeHeader {

    private String key;
    private Object value;
    private String definition;
    private long lastUpdate;
    private long timeout = MPeriod.MINUTE_IN_MILLISECOUNDS;

    public TypeHeader(String key, String value) {
        this.key = key;
        if (value.startsWith("$"))
            this.definition = value;
        else
            this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefinition() {
        return definition;
    }

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

}
