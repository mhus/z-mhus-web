package de.mhus.cherry.portal.impl.api;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.impl.AbstractServlet;

@Component(provide = Servlet.class, properties = { "alias=/.api" }, name="CherryApiServlet",servicefactory=true)
public class CherryApiServlet extends AbstractServlet {

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
	protected void doService(CallContext call, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		call.getVirtualHost().processApiRequest(call);
	}

	@Override
	public String getName() {
		return "api";
	}
	

}
