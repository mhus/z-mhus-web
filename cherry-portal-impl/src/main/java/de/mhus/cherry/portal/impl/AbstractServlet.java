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
		
		// check general security
		SecurityApi sec = Sop.getApi(SecurityApi.class, false);
		sec.checkHttpRequest(req, res);
		if (res.isCommitted()) return;
		
		// load environment
		InternalCherryApi cherry = Sop.getApi(InternalCherryApi.class);
		CallContext call = cherry.createCall( this, req, res );
		if (res.isCommitted()) return;

		// check host specific security
		// 1) host access general
		{
			List<String> list = call.getVirtualHost().getConfigurationList(CherryApi.CONFIG_HOST_ALLOWED);
			if (list != null) {
				String host = req.getRemoteHost();
				boolean found = false;
				for (String item : list)
					if (host.matches(item)) {
						found = true;
						break;
					}
				if (!found) {
					call.getVirtualHost().sendError(call, HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}
		}
		// 2) Check access for this servlet
		{
			List<String> list = call.getVirtualHost().getConfigurationList(CherryApi.CONFIG_HOST_ALLOWED + "_" + getName());
			if (list != null) {
				String host = req.getRemoteHost();
				boolean found = false;
				for (String item : list)
					if (host.matches(item)) {
						found = true;
						break;
					}
				if (!found) {
					call.getVirtualHost().sendError(call, HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}
		}
		
        try {
        	doService(call, req, res);
        } finally {
        	cherry.releaseCall(call);
        }
	}

	@Override
	public abstract String getName();

	protected abstract void doService(CallContext call, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;

}
