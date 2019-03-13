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
