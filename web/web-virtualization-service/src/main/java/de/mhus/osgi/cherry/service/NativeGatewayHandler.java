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

@Component(immediate=true,provide=CentralRequestHandler.class,name="NativeGatewayHandler")
public class NativeGatewayHandler extends AbstractCentralRequestHandler {
	
	@Override
	public boolean doHandleBefore(CentralCallContext context)
			throws IOException, ServletException {
				
		VirtualHost vh = (VirtualHost) context.getAttribute(VirtualHost.CENTRAL_CONTEXT_KEY);
		if (vh != null) {
			if (!vh.allowNativeRequest(context.getTarget())) {
				context.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
				return true;
			}
		}
		return false;
	}

	@Override
	public void doHandleAfter(CentralCallContext context) throws IOException,
			ServletException {
	}
	
	@Override
	public double getSortHint() {
		return 100;
	}

	@Override
	public void configure(Properties rules) {
		
	}

}
