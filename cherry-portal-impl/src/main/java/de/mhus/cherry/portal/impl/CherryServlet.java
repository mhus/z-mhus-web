package de.mhus.cherry.portal.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;

@Component(provide = Servlet.class, properties = "alias=/*", name="CherryServlet",servicefactory=true)
public class CherryServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doService(CallContext call, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		call.getVirtualHost().processRequest(call);
	}

	@Override
	public String getName() {
		return "content";
	}
	
}
