package de.mhus.cherry.web.api;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface VirtualHost {

	public void sendError(CallContext context, int sc);

	public void doRequest(CallContext context);

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

	public String getVirtualHostAlias();

	public void start(CherryApi api) throws MException;

	public void stop(CherryApi api);

	public void setBundle(Bundle bundle);
	
	public Bundle getBundle();

	/**
	 * Return true if the filters where successful and the page can be displayed.
	 * 
	 * @param call
	 * @return true if ok
	 * @throws MException 
	 */
	public boolean doFiltersBegin(CallContext call) throws MException;

	public void doFiltersEnd(CallContext call) throws MException;
	
}
