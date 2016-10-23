package de.mhus.cherry.portal.impl;

import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import de.mhus.cherry.portal.api.ProcessorContext;
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;

public class DefaultSessionContext implements SessionContext {

	private CherryServlet servlet;
	private HttpSession session;
	private VirtualHost virtualHost;

	public DefaultSessionContext(CherryServlet servlet, VirtualHost vHost, HttpSession session) {
		this.servlet = servlet;
		this.session = session;
		this.virtualHost = vHost;
	}

	@Override
	public VirtualHost getVirtualHost() {
		return virtualHost;
	}

	@Override
	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	@Override
	public HttpServlet getHttpServlet() {
		return servlet;
	}

}
