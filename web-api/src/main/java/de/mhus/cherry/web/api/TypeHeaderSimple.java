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
        if (add)
            resp.addHeader(key, value);
        else
            resp.setHeader(key, value);
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
