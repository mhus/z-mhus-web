package de.mhus.osgi.cherry.api.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import de.mhus.lib.core.logging.Log;

public abstract class AbstractServletContext implements ServletContext {

	protected String servletName = "iam";
	protected HashMap<String, Object> attr = new HashMap<>(); 
	protected HashMap<String, String> param = new HashMap<>();
	protected String serverInfo = "JavaServer Web Dev Kit/1.0"; 
	protected Log log;

	@Override
	public ServletContext getContext(String uripath) {
		return this;
	}

	@Override
	public int getMajorVersion() {
		return 2;
	}

	@Override
	public int getMinorVersion() {
		return 5;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		return null;
	}

	@Override
	public Enumeration getServlets() {
		Vector<Object> out = new Vector();
		return out.elements();
	}

	@Override
	public Enumeration getServletNames() {
		Vector<String> out = new Vector();
		//out.add(servletName);
		return out.elements();
	}

	@Override
	public void log(String msg) {
		if (log != null) log.i(msg);
	}

	@Override
	public void log(Exception exception, String msg) {
		if (log != null) log.i(msg, exception);
		
	}

	@Override
	public void log(String message, Throwable throwable) {
		if (log != null) log.i(message, throwable);
	}

	@Override
	public String getServerInfo() {
		return serverInfo ;
	}

	@Override
	public String getInitParameter(String name) {
		return param.get(name);
	}

	@Override
	public Enumeration getInitParameterNames() {
		return new Vector<>(param.keySet()).elements();
	}

	@Override
	public Object getAttribute(String name) {
		return attr.get(name);
	}

	@Override
	public Enumeration getAttributeNames() {
		return new Vector<>(attr.keySet()).elements();
	}

	@Override
	public void setAttribute(String name, Object object) {
		attr.put(name, object);
	}

	@Override
	public void removeAttribute(String name) {
		attr.remove(name);
	}

	@Override
	public String getServletContextName() {
		return servletName ;
	}

}
