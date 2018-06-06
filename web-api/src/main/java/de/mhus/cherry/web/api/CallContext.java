package de.mhus.cherry.web.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CallContext {

	String REQUEST_ATTRIBUTE_NAME = "__cherry_call_context";

	HttpServletRequest getHttpRequest();
	HttpServletResponse getHttpResponse();
	
	VirtualHost getVirtualHost();
	String getHttpPath();
	HttpServlet getHttpServlet();
	String getHttpMethod();
	SessionContext getSession();
	void setAttribute(String name, Object value);
	Object getAttribute(String name);
	String getSessionId();
	String getMimeType(String file);
	
}
