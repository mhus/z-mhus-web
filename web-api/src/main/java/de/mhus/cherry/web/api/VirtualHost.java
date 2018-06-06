package de.mhus.cherry.web.api;

import java.io.IOException;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.config.IConfig;

public interface VirtualHost {

	public void sendError(CallContext context, int sc) throws IOException;

	public void processRequest(CallContext context);

	/**
	 * Get WebSpace configuration'
	 * 
	 * @return Config object
	 */
	IConfig getConfig();
	
	/**
	 * Web Space specific properties.
	 * @return Properties container
	 */
	IProperties getProperties();
	
}
