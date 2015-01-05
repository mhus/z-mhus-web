package de.mhus.osgi.cherry.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebResourceRequest  implements ResourceRequest {

	private HttpServletRequest request;

	public WebResourceRequest(HttpServletRequest request) {
		this.request = request;
	}

	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	public String getAuthType() {
		return request.getAuthType();
	}

	public Cookie[] getCookies() {
		return request.getCookies();
	}

	public Enumeration getAttributeNames() {
		return request.getAttributeNames();
	}

	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(env);
	}

	public int getContentLength() {
		return request.getContentLength();
	}

	public String getContentType() {
		return request.getContentType();
	}

	public String getParameter(String name) {
		return request.getParameter(name);
	}

	public String getMethod() {
		return request.getMethod();
	}

	public String[] getParameterValues(String name) {
		return request.getParameterValues(name);
	}

	public String getScheme() {
		return request.getScheme();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getServerName() {
		return request.getServerName();
	}

	public int getServerPort() {
		return request.getServerPort();
	}

	public BufferedReader getReader() throws IOException {
		return request.getReader();
	}

	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	public boolean isUserInRole(String role) {
		return request.isUserInRole(role);
	}

	public void setAttribute(String name, Object o) {
		request.setAttribute(name, o);
	}

	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
	}

	public void removeAttribute(String name) {
		request.removeAttribute(name);
	}

	public Locale getLocale() {
		return request.getLocale();
	}

	public boolean isSecure() {
		return request.isSecure();
	}


	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid();
	}


	@Override
	public InputStream getPortletInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public boolean isWindowStateAllowed(WindowState state) {
		return false;
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode mode) {
		return false;
	}

	@Override
	public PortletMode getPortletMode() {
		return null;
	}

	@Override
	public WindowState getWindowState() {
		return null;
	}

	@Override
	public PortletPreferences getPreferences() {
		return null;
	}

	@Override
	public PortletSession getPortletSession() {
		return null;
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		return null;
	}

	@Override
	public String getProperty(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getProperties(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return null;
	}

	@Override
	public PortalContext getPortalContext() {
		return null;
	}

	@Override
	public String getWindowID() {
		return null;
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		return null;
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {
		return null;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public String getResourceID() {
		return null;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		return null;
	}

	@Override
	public String getResponseContentType() {
		return null;
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		return null;
	}

	@Override
	public String getCacheability() {
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

}
