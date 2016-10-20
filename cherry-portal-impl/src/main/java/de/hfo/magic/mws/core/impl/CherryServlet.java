package de.hfo.magic.mws.core.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.hfo.magic.mws.core.api.CherryNavigationProvider;
import de.hfo.magic.mws.core.api.CherryUtil;
import de.mhus.lib.cao.CaoNode;

@Component(provide = Servlet.class, properties = "alias=/*", name="CherryServlet",servicefactory=true)
public class CherryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		String host = req.getHeader("Host");
		CherryNavigationProvider navProvider = CherryUtil.findNavigationProvider(host);
		
		if (navProvider == null) {
			res.sendError(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}
		
		String path = req.getPathInfo();
		CaoNode navResource = navProvider.getNode(path);
		if (navResource == null) {
			navProvider.sendError(res, path, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		navProvider.processRequest( req, res, navResource );
		
	}
	
}
