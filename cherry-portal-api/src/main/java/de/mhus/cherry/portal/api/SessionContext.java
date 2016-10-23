package de.mhus.cherry.portal.api;

import javax.servlet.http.HttpServlet;

public interface SessionContext {

	VirtualHost getVirtualHost();

	void setAttribute(String name, Object value);
	
	Object getAttribute(String name);

	HttpServlet getHttpServlet();

}
