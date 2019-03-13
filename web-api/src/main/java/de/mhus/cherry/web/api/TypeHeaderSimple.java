package de.mhus.cherry.web.api;

import javax.servlet.http.HttpServletResponse;

public class TypeHeaderSimple implements TypeHeader {

    private String key;
    private String value;

    public TypeHeaderSimple(String key, String value) {
        this.key = key;
        this.value = value;
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
        resp.setHeader(key, value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
