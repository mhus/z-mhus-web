package de.mhus.cherry.editor.impl;

import java.io.IOException;

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
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.basics.Named;
import de.mhus.lib.core.MApi;
import de.mhus.lib.form.ActivatorAdapterProvider;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = Servlet.class, properties = { "alias=/.control" }, name="CHERRYGUI",servicefactory=true)
@VaadinServletConfiguration(ui=ControlUi.class, productionMode=false)
public class ControlServlet extends VaadinServlet implements Named {

	private static final long serialVersionUID = 1L;
	private BundleContext context;
	
	@Activate
	public void activate(ComponentContext ctx) {
		this.context = ctx.getBundleContext();
		MApi.get().getBaseControl().getCurrentBase().addObject(ActivatorAdapterProvider.class,new CherryActivatorAdapterProvider());
	}
	
	public BundleContext getBundleContext() {
		return context;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// load environment
		CallContext call = CherryUtil.prepareHttpRequest(this, request, response);
		if (call == null) return;
		
		try {
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
				// remember the last call object
//				vaadinSession.setAttribute("__vc", call);
    		} catch (Throwable t) {
    			
    		}
			
			Sop.getApi(InternalCherryApi.class).releaseCall(call);

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
