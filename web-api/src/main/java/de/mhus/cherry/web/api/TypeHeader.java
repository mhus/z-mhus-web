package de.mhus.cherry.web.api;

import javax.servlet.http.HttpServletResponse;

public interface TypeHeader {

    public String getKey();
    
    public Object getValue();

    public void setValue(String value);

    public void appendTo(HttpServletResponse resp);

}
