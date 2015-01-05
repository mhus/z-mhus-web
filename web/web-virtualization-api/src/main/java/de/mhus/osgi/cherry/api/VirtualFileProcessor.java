package de.mhus.osgi.cherry.api;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.central.CentralCallContext;

public interface VirtualFileProcessor {

	boolean processRequest(VirtualHost host, ResourceNode res, CentralCallContext context) throws Exception;

	ProcessorMatcher getDefaultMatcher();
	
}
