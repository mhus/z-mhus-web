package de.mhus.cherry.portal.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.basics.Named;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.security.SecurityApi;

public abstract class AbstractServlet extends HttpServlet implements Named {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		CallContext call = CherryUtil.prepareHttpRequest(this, req, res);
		if (call == null) return;
		
        try {
        	doService(call, req, res);
        } finally {
        	Sop.getApi(InternalCherryApi.class).releaseCall(call);
        }
	}

	@Override
	public abstract String getName();

	protected abstract void doService(CallContext call, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;

}
