package de.mhus.cherry.web.api;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface VirtualHost {

	void sendError(CallContext context, int sc);

	void doRequest(CallContext context);

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

	String[] getVirtualHostAliases();

	void start(CherryApi api) throws MException;

	void stop(CherryApi api);

	void setBundle(Bundle bundle);
	
	Bundle getBundle();

	/**
	 * Return true if the filters where successful and the page can be displayed.
	 * 
	 * @param call
	 * @return true if ok
	 * @throws MException 
	 */
	boolean doFiltersBegin(CallContext call) throws MException;

	void doFiltersEnd(CallContext call) throws MException;
	
	String getMimeType(String file);

	String getName();

}
