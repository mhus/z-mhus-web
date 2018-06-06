package de.mhus.cherry.web.impl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.SessionContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MSystem;

public class CherryCallContext implements CallContext {

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private String httpPath;
	private VirtualHost virtualHost;
	private HttpServlet httpServlet;
	private String sessionId;
	private SessionContext sessionContext;

	public void setHttpRequest(HttpServletRequest req) {
		httpRequest = req;
		if (req == null) return;
		httpPath = req.getPathInfo();
		req.setAttribute(CallContext.REQUEST_ATTRIBUTE_NAME, this);
		sessionId = req.getSession().getId();
		sessionContext = CherryApiImpl.instance().getCherrySession(sessionId);
	}

	public void setHttpResponse(HttpServletResponse res) {
		httpResponse = res;
	}

	@Override
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	@Override
	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	@Override
	public String getHttpPath() {
		return httpPath;
	}

	public void setVirtualHost(VirtualHost vHost) {
		virtualHost = vHost;
	}

	public void setHttpServlet(HttpServlet servlet) {
		httpServlet = servlet;
	}

	@Override
	public VirtualHost getVirtualHost() {
		return virtualHost;
	}

	@Override
	public HttpServlet getHttpServlet() {
		return httpServlet;
	}

	@Override
	public String getHttpMethod() {
		if (httpRequest.getParameter("_method") != null) return httpRequest.getParameter("_method").toLowerCase();
		return httpRequest.getMethod().toLowerCase();
	}

	@Override
	public SessionContext getSession() {
		return sessionContext;
	}

	@Override
	public void setAttribute(String name, Object value) {
		httpRequest.setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return httpRequest.getAttribute(name);
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, httpPath);
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String getMimeType(String file) {
		String extension = MFile.getFileSuffix(file);
		return MFile.getMimeType(extension);
	}

}
