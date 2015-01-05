package de.mhus.osgi.cherry.api;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.central.CentralCallContext;

public interface VirtualApplication {

	String CENTRAL_CONTEXT_KEY = "VirtualApplication";

	boolean processRequest(VirtualHost host, CentralCallContext context) throws Exception;

	void configureHost(VirtualHost host, ResourceNode applicationConfig) throws Exception;

	void processError(VirtualHost host, CentralCallContext context, int cs);

	ResourceNode getResource(VirtualHost host, String target);

	ClassLoader getApplicationClassLoader();

}
