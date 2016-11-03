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
import de.mhus.cherry.portal.api.SessionContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

@Component(provide = Servlet.class, properties = "alias=/*", name="CherryServlet",servicefactory=true)
public class CherryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
			
		CherryApi cherry = Sop.getApi(CherryApi.class);
		String host = req.getHeader("Host");
		VirtualHost vHost = cherry.findVirtualHost(host);
		
		
        AccessApi access = Sop.getApi(AccessApi.class);
        AaaContext context = cherry.getContext( req.getSession().getId() );
        if (context == null) context = vHost.doLogin(new de.mhus.lib.servlet.HttpServletRequestWrapper(req));
        access.process(context);
        
        try {
						
			CherryCallContext callContext = new CherryCallContext();
			callContext.setHttpRequest(req);
			callContext.setHttpResponse(new CherryResponseWrapper(res));
			callContext.setVirtualHost(vHost);
			callContext.setHttpServlet(this);
			vHost.processRequest(callContext);
			
        } finally {
        	access.release(context);
        }
	}
	
}
