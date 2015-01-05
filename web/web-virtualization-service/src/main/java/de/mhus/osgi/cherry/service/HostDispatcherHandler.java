package de.mhus.osgi.cherry.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.VirtualHostProvider;
import de.mhus.osgi.cherry.api.central.AbstractCentralRequestHandler;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.api.central.CentralRequestHandler;
import de.mhus.osgi.cherry.api.util.ExtendedServletResponse;

@Component(immediate=true,provide=CentralRequestHandler.class,name="HostDispatcherHandler")
public class HostDispatcherHandler extends AbstractCentralRequestHandler {

	private HashMap<String, VirtualHostProvider> hostProviders = new HashMap<>();
	private ServiceTracker<VirtualHostProvider, VirtualHostProvider> tracker;
	private BundleContext bc;

	@Activate
	public void doActivate(ComponentContext ctx) {
		bc = ctx.getBundleContext();
		tracker = new ServiceTracker<VirtualHostProvider,VirtualHostProvider>(bc, VirtualHostProvider.class, new WSCustomizer() );
		tracker.open();
	}
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		tracker.close();
	}
	
	@Override
	public boolean doHandleBefore(CentralCallContext context)
			throws IOException, ServletException {
		
		ExtendedServletResponse.inject(context);
		String host = context.getHost();
		
		VirtualHostProvider provider = findHostProvider(host);
		VirtualHost vh = null;
		
		if (provider == null)
			provider = findDefaultHostProvider();
		
		if (provider != null) {
			vh = provider.getHost(host);
			if (vh != null) {
				context.setAttribute(VirtualHostProvider.CENTRAL_CONTEXT_KEY, provider);
				context.setAttribute(VirtualHost.CENTRAL_CONTEXT_KEY, vh);
			}
		}
		
		if (vh == null) {
			context.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
			return true;
		}
		
		return false;
	}

	private VirtualHostProvider findDefaultHostProvider() {
		synchronized (hostProviders) {
			return hostProviders.get("*");
		}
	}

	private VirtualHostProvider findHostProvider(String host) {
		synchronized (hostProviders) {
			return hostProviders.get(host);
		}
	}

	@Override
	public void doHandleAfter(CentralCallContext context) throws IOException,
			ServletException {
		
		VirtualHost vh = (VirtualHost) context.getAttribute(VirtualHost.CENTRAL_CONTEXT_KEY);
		if (vh != null && ExtendedServletResponse.isExtended(context)) {
			int s = ExtendedServletResponse.getExtendedResponse(context).getStatus();
			if (s != 0 && s != HttpServletResponse.SC_OK)
				vh.processError(context);
		}
	}
	
	@Override
	public double getSortHint() {
		return -10;
	}

	@Override
	public void configure(Properties rules) {
		
	}

	private class WSCustomizer implements ServiceTrackerCustomizer<VirtualHostProvider, VirtualHostProvider> {

		@Override
		public VirtualHostProvider addingService(
				ServiceReference<VirtualHostProvider> reference) {

			VirtualHostProvider service = bc.getService(reference);
			
			synchronized (hostProviders) {
				for (String host : service.getProvidedHosts())
					hostProviders.put(host, service);
			}	
			return service;
		}

		@Override
		public void modifiedService(
				ServiceReference<VirtualHostProvider> reference,
				VirtualHostProvider service) {

			synchronized (hostProviders) {
				HashSet<String> remove = new HashSet<>();
				for (Map.Entry<String, VirtualHostProvider> entry : hostProviders.entrySet())
					if (entry.getValue().equals(service))
						remove.add(entry.getKey());
				
				for (String host : service.getProvidedHosts()) {
					hostProviders.put(host, service);
					remove.remove(host);
				}
				
				for (String host : remove)
					hostProviders.remove(host);
			}
			
		}

		@Override
		public void removedService(
				ServiceReference<VirtualHostProvider> reference,
				VirtualHostProvider service) {
			synchronized (hostProviders) {
				HashSet<String> remove = new HashSet<>();
				for (Map.Entry<String, VirtualHostProvider> entry : hostProviders.entrySet())
					if (entry.getValue().equals(service))
						remove.add(entry.getKey());
								
				for (String host : remove)
					hostProviders.remove(host);
			}
			
		}
		
	}

}
