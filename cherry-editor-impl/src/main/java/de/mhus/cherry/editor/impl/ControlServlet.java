package de.mhus.cherry.editor.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.lib.basics.Named;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.security.SecurityApi;

@Component(provide = Servlet.class, properties = { "alias=/.control" }, name="CHERRYGUI",servicefactory=true)
@VaadinServletConfiguration(ui=ControlUi.class, productionMode=true)
public class ControlServlet extends VaadinServlet implements Named {

	private static final long serialVersionUID = 1L;
	private BundleContext context;
	
	@Activate
	public void activate(ComponentContext ctx) {
		this.context = ctx.getBundleContext();
	}
	
	public BundleContext getBundleContext() {
		return context;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// check general security
		SecurityApi sec = Sop.getApi(SecurityApi.class, false);
		sec.checkHttpRequest(request, response);
		if (response.isCommitted()) return;

		// load environment
		CallContext call = null;
		try {
			call = Sop.getApi(InternalCherryApi.class).createCall(this, request, response);
			if (response.isCommitted()) return;
			
			// check host specific security
			// 1) host access general
			{
				List<String> list = call.getVirtualHost().getConfigurationList(CherryApi.CONFIG_HOST_ALLOWED);
				if (list != null) {
					String host = request.getRemoteHost();
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
					String host = request.getRemoteHost();
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

			
			super.service(request, response);
		} finally {
			// cleanup
			try {
				VaadinSession vaadinSession = (VaadinSession) request.getAttribute("__vs");
				if (vaadinSession != null) {
		    		for (UI ui : vaadinSession.getUIs()) {
		    			if (ui instanceof ControlUi)
		    				((ControlUi)ui).requestEnd();
		    		}
				}
    		} catch (Throwable t) {
    			
    		}
			
			if (call != null) {
				Sop.getApi(InternalCherryApi.class).releaseCall(call);
				call = null;
			}

		}
	}

    public String getName() {
		return "control";
	}

	@Override
	protected boolean isStaticResourceRequest(HttpServletRequest request) {
    	// set user and trace ...
        
    	boolean ret = super.isStaticResourceRequest(request);
    	if (!ret) {
    		try {
	    		VaadinServletRequest vs = createVaadinRequest(request);
	    		VaadinSession vaadinSession = getService().findVaadinSession(vs);
	    		request.setAttribute("__vs", vaadinSession);
	    		for (UI ui : vaadinSession.getUIs()) {
	    			if (ui instanceof ControlUi)
	    				((ControlUi)ui).requestBegin(request);
	    		}
    		} catch (Throwable t) {
    			
    		}
    	}
    	return ret;
    }
    
}
