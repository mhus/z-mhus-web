package de.mhus.cherry.web.api;

import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CallContext {

	String REQUEST_ATTRIBUTE_NAME = "__cherry_call_context";

	HttpServletRequest getHttpRequest();
	HttpServletResponse getHttpResponse();
	
	VirtualHost getVirtualHost();
	String getHttpPath();
	Servlet getHttpServlet();
	String getHttpMethod();
	WebSession getSession();
	void setAttribute(String name, Object value);
	Object getAttribute(String name);
	String getSessionId();
	boolean isSession();
	String getHttpHost();
	
	/**
	 * Use this to get the output stream from http response to get the filter chain stream.
	 * 
	 * @return current output stream
	 */
	OutputStream getOutputStream();
	
	/**
	 * This will return a writer bound to the output stream
	 * @return The writer
	 */
	Writer getWriter();
}
