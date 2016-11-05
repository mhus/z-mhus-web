package de.mhus.cherry.portal.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;

public interface CallContext {

	String REQUEST_ATTRIBUTE_NAME = "__cherry_call_context";

	HttpServletRequest getHttpRequest();

	HttpServletResponse getHttpResponse();

	String getHttpPath();

	CaoNode getNavigationResource();

	CaoNode getResource();

	CaoNode getMainResource();
	
	VirtualHost getVirtualHost();

	HttpServlet getHttpServlet();

	String getReturnType();

	String[] getSelectors();

	String getHttpMethod(); // returns the current http method in lower case !!!
	
	IProperties getSessionContext();

	void setAttribute(String name, Object value);
	
	Object getAttribute(String name);
	
	void resetPath();
	
	String consumePath();
	
}
