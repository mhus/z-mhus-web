package de.mhus.cherry.portal.impl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MSystem;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public class CherryCallContext implements CallContext {

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private String httpPath;
	private CaoNode navigationResource;
	private CaoNode resource;
	private VirtualHost virtualHost;
	private HttpServlet httpServlet;
	private String returnType;
	private String[] selectors;
	private IProperties sessionContext;
	private CaoNode mainResource;
	private String[] path;
	private int pathCnt;
	private AaaContext aaaContext;

	public void setHttpRequest(HttpServletRequest req) {
		httpRequest = req;
		httpPath = req.getPathInfo();
		req.setAttribute(CallContext.REQUEST_ATTRIBUTE_NAME, this);
		sessionContext = Sop.getApi(InternalCherryApi.class).getCherrySession(req.getSession().getId());
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
		if (httpRequest.getParameter("_method") != null) return httpRequest.getParameter("_method").toLowerCase();
		return httpRequest.getMethod().toLowerCase();
	}

	@Override
	public IProperties getSession() {
		return sessionContext;
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

	@Override
	public void resetPath() {
		if (httpPath == null) return;
		path = httpPath.split("/");
		pathCnt = 1; // ignore element 0
	}

	@Override
	public String consumePath() {
		if (path == null || path.length < pathCnt)
			return null;
		pathCnt++;
		return path[pathCnt-1];
	}

	public void setCurrentAaaContext(AaaContext context) {
		this.aaaContext = context;
	}

	@Override
	public AaaContext getAaaContext() {
		return aaaContext;
	}

}
