package de.mhus.osgi.cherry.api;

import java.util.HashMap;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.portlet.resource.Resource;
import de.mhus.osgi.cherry.api.central.CentralCallContext;

public interface ApplicationContext {

	public void doActivate(VirtualApplication defaultApplication,
			VirtualHost host, ResourceNode config) throws Exception;
	
	public boolean deliverContent(VirtualHost host, CentralCallContext context, ResourceNode res) throws Exception;
	
	public void processError(CentralCallContext context, int cs);
	
	public Resource getResourceHandler(CentralCallContext context);
	
	public HashMap<String, ProcessorMatcher> getProcessorMapping();

	public ResourceNode findIndex(CentralCallContext context, ResourceNode res);
	
}
