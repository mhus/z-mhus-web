package de.mhus.cherry.portal.api.util;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.logging.Log;

public abstract class AbstractServletContext implements ServletContext {

	protected String servletName = "iam";
	protected HashMap<String, Object> attr = new HashMap<>(); 
	protected HashMap<String, String> param = new HashMap<>();
	protected String serverInfo = "JavaServer Web Dev Kit/1.0"; 
	protected Log log;
	protected MProperties init = new MProperties();

	@Override
	public ServletContext getContext(String uripath) {
		return this;
	}

	@Override
	public int getMajorVersion() {
		return 3;
	}

	@Override
	public int getMinorVersion() {
		return 1;
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


	@Override
	public int getEffectiveMajorVersion() {
		return 3;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 1;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		init.setString(name, value);
		return true;
	}

	@Override
	public Dynamic addServlet(String servletName, String className) {
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return null;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return null;
	}

	@Override
	public void addListener(String className) {
		
	}

	@Override
	public <T extends EventListener> void addListener(T t) {
		
	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public void declareRoles(String... roleNames) {
		
	}

	@Override
	public String getVirtualServerName() {
		return null;
	}

}
