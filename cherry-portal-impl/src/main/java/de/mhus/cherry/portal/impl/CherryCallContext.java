package de.mhus.cherry.portal.impl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MSystem;

public class CherryCallContext implements CallContext {

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private String httpPath;
	private CaoNode navigationResource;
	private CaoNode resource;
	private VirtualHost virtualHost;
	private CherryServlet httpServlet;
	private String returnType;
	private String[] selectors;
	private SessionContext sessionContext;
	private CaoNode mainResource;

	public void setHttpRequest(HttpServletRequest req) {
		httpRequest = req;
		httpPath = req.getPathInfo();
		req.setAttribute(CallContext.REQUEST_ATTRIBUTE_NAME, this);
	}

	public void setHttpResponse(HttpServletResponse res) {
		httpResponse = res;
	}

	public void setNavigationResource(CaoNode resource) {
		navigationResource = resource;
	}

	public void setResource(CaoNode resource) {
		this.resource = resource;
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

	@Override
	public CaoNode getNavigationResource() {
		return navigationResource;
	}

	@Override
	public CaoNode getResource() {
		return resource;
	}

	public void setVirtualHost(VirtualHost vHost) {
		virtualHost = vHost;
	}

	public void setHttpServlet(CherryServlet servlet) {
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

	public void setReturnType(String retType) {
		returnType = retType;
	}

	public void setSelectors(String[] selectors) {
		this.selectors = selectors;
	}

	@Override
	public String getReturnType() {
		return returnType;
	}

	@Override
	public String[] getSelectors() {
		return selectors;
	}

	@Override
	public String getHttpMethod() {
		return httpRequest.getMethod();
	}

	@Override
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	@Override
	public CaoNode getMainResource() {
		return mainResource;
	}

	public void setMainResource(CaoNode mainResource) {
		this.mainResource = mainResource;
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

}
