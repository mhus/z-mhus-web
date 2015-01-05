package de.mhus.osgi.cherry.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;
import de.mhus.lib.portlet.resource.Resource;
import de.mhus.osgi.cherry.api.ApplicationContext;
import de.mhus.osgi.cherry.api.ProcessorMatcher;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualFileProcessor;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.central.CentralCallContext;

/**
 * This is a factory for Application Context
 * @author mikehummel
 *
 */
@Component(name="DefaultApplication",immediate=true,properties="name=" + DefaultVirtualHost.DEFAULT_APPLICATION_ID)
public class DefaultApplication implements VirtualApplication {

	protected HashMap<String, ProcessorMatcher> processorMapping = new HashMap<>();
	protected HashMap<String, VirtualFileProcessor> processors = new HashMap<>();
	protected ServiceTracker<VirtualFileProcessor, VirtualFileProcessor> processorTracker;
	
	protected BundleContext bc;

	@Activate
	public void doActivate(ComponentContext ctx) {
		bc = ctx.getBundleContext();
		processorTracker = new ServiceTracker<>(bc, VirtualFileProcessor.class, new MyCustomizer());
		processorTracker.open();
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		processorTracker.close();
	}
	
	@Override
	public boolean processRequest(VirtualHost host, CentralCallContext context)
			throws Exception {

		ApplicationContext app = (ApplicationContext)host.getAttribute(CENTRAL_CONTEXT_KEY);
		if (app == null) {
			context.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return true;
		}

		context.getRequest().setAttribute(CENTRAL_CONTEXT_KEY, app);
		
		Resource resHandler =  app.getResourceHandler(context);
		if (resHandler != null) {
			if (resHandler.serveResource(context.getTarget(), new WebResourceRequest(context.getRequest()), new WebResourceResponse(context.getResponse()) ))
				return true;
		}
		
		ResourceNode res = host.getResource(context.getTarget());
		
		if (res != null && !res.hasContent()) {
			// find index
			res = app.findIndex(context, res);
			if (res != null)
				context.setTarget( context.getTarget() + "/" + res.getName());
		}
		
		if (res != null) {
			
			// lookup for processor

			HashMap<String, ProcessorMatcher> mapping = app.getProcessorMapping();
			if (mapping == null) mapping = processorMapping;
			
			for (Map.Entry<String, ProcessorMatcher> entry : mapping.entrySet()) {
				if (entry.getValue().matches(res)) {
					String processorName = entry.getValue().getProcessor();
					if (processorName != null) {
						VirtualFileProcessor processor = processors.get(processorName);
						if (processor != null) {
							return processor.processRequest(host, res, context);
						}
					}
					// do not deliver source code content
					context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
					return true;
				}
			}
			
			// not found, deliver as static content
			app.deliverContent(host, context, res);
			return true;
		}
		return false;		
//--		
		

	}

	@Override
	public void configureHost(VirtualHost host, ResourceNode config) throws Exception {

		ApplicationContext context = createApplicationContext(host, config);
		host.setAttribute(CENTRAL_CONTEXT_KEY, context);
		
	}

	protected ApplicationContext createApplicationContext(
			VirtualHost host, ResourceNode config) throws Exception {
		
		if (config != null) {
			try {
				String className = config.getExtracted("class");
				if (className != null) {
					Class<?> clazz = host.getHostClassLoader().loadClass(className);
					ApplicationContext app = (ApplicationContext)clazz.newInstance();
					app.doActivate(this, host, config);
					return app;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		DefaultApplicationContext app = new DefaultApplicationContext();
		app.doActivate(this, host, config);
		return app;
	}

	@Override
	public void processError(VirtualHost host, CentralCallContext context, int cs) {
		DefaultApplicationContext app = (DefaultApplicationContext)host.getAttribute(CENTRAL_CONTEXT_KEY);
		context.getRequest().setAttribute(CENTRAL_CONTEXT_KEY, app);
		app.processError(context, cs);
	}
	
	private class MyCustomizer implements ServiceTrackerCustomizer<VirtualFileProcessor,VirtualFileProcessor> {

		@Override
		public VirtualFileProcessor addingService(
				ServiceReference<VirtualFileProcessor> reference) {
			
			VirtualFileProcessor service = bc.getService(reference);
			String name = (String) reference.getProperty("name");
			if (name != null) {
				processors.put(name, service);
				processorMapping.put(name, service.getDefaultMatcher());
			}
			return service;
		}

		@Override
		public void modifiedService(
				ServiceReference<VirtualFileProcessor> reference,
				VirtualFileProcessor service) {
			String name = (String) reference.getProperty("name");
			if (name != null) {
				processorMapping.put(name, service.getDefaultMatcher());
			}
		}

		@Override
		public void removedService(
				ServiceReference<VirtualFileProcessor> reference,
				VirtualFileProcessor service) {
			String name = (String) reference.getProperty("name");
			if (name != null) {
				processors.remove(name);
				processorMapping.remove(name);
			}
		}
		
	}

	@Override
	public ResourceNode getResource(VirtualHost host, String target) {
		return ((DefaultVirtualHost)host).getDocumentRootResource(target);
	}

	@Override
	public ClassLoader getApplicationClassLoader() {
		return getClass().getClassLoader();
	}
	
	
}
