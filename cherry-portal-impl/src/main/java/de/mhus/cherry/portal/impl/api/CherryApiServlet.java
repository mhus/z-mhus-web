package de.mhus.cherry.portal.impl.api;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.impl.CherryCallContext;
import de.mhus.cherry.portal.impl.CherryResponseWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

@Component(provide = Servlet.class, properties = { "alias=/.api" }, name="CherryApiServlet",servicefactory=true)
public class CherryApiServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static CherryApiServlet instance;

    @Activate
    public void activate(ComponentContext ctx) {
		instance = this;
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	instance = null;
    }

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		InternalCherryApi cherry = Sop.getApi(InternalCherryApi.class);
		CallContext callContext = cherry.createCall(this, req, resp);
		if (resp.isCommitted()) return;
		
        try {

			callContext.getVirtualHost().processApiRequest(callContext);
			
        } finally {
        	cherry.releaseCall(callContext);
        }

	}
	

}
