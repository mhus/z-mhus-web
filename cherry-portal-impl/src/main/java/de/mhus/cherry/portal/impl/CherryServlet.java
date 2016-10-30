package de.mhus.cherry.portal.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = Servlet.class, properties = "alias=/*", name="CherryServlet",servicefactory=true)
public class CherryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
			
		String host = req.getHeader("Host");
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		
		CherryCallContext callContext = new CherryCallContext();
		callContext.setHttpRequest(req);
		callContext.setHttpResponse(new CherryResponseWrapper(res));
		callContext.setVirtualHost(vHost);
		callContext.setHttpServlet(this);
		HttpSession httpSession = req.getSession();
		synchronized (httpSession) {
			SessionContext cherrySession = (SessionContext) httpSession.getAttribute("__cherry_session");
			if (cherrySession == null) {
				cherrySession = new DefaultSessionContext(this, vHost, httpSession);
				httpSession.setAttribute("__cherry_session", cherrySession);
			}
			callContext.setSessionContext(cherrySession);
		}
		vHost.processRequest(callContext);
		
	}
	
}
