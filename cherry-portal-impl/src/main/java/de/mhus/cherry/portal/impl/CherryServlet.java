package de.mhus.cherry.portal.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryUtil;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;

@Component(provide = Servlet.class, properties = "alias=/*", name="CherryServlet",servicefactory=true)
public class CherryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static CherryServlet instance;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	
		instance = this; //TODO on startup
		
		String host = req.getHeader("Host");
		VirtualHost vHost = CherryUtil.findVirtualHost(host);
		NavigationProvider navProvider = vHost.getNavigationProvider();
		
		if (navProvider == null) {
			res.sendError(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}
		
		String path = req.getPathInfo();
		CaoNode navResource = navProvider.getNode(path);
		if (navResource == null) {
			vHost.sendError(res, path, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		vHost.processRequest( req, res, navResource );
		
	}
	
}
