package de.mhus.osgi.cherry.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.VirtualHostProvider;
import de.mhus.osgi.cherry.api.central.AbstractCentralRequestHandler;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.api.central.CentralRequestHandler;
import de.mhus.osgi.cherry.api.util.ExtendedServletResponse;

@Component(immediate=true,provide=CentralRequestHandler.class,name="HostRequestHandler")
public class HostRequestHandler extends AbstractCentralRequestHandler {
	
	@Override
	public boolean doHandleBefore(CentralCallContext context)
			throws IOException, ServletException {

		try {
			VirtualHost vh = (VirtualHost) context.getAttribute(VirtualHost.CENTRAL_CONTEXT_KEY);
			if (vh != null) {
				return vh.processRequest(context);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			context.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return true;
		}
		return false;
	}

	@Override
	public void doHandleAfter(CentralCallContext context) throws IOException,
			ServletException {
	}
	
	@Override
	public double getSortHint() {
		return 0;
	}

	@Override
	public void configure(Properties rules) {
		
	}

}
