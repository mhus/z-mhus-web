package de.mhus.cherry.web.api;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface VirtualHost {

	void sendError(CallContext call, int sc, Throwable t);

	void doRequest(InternalCallContext call);

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

	Set<String> getVirtualHostAliases();

	void start(CherryApi api) throws MException;

	void stop(CherryApi api);

	void setBundle(Bundle bundle);
	
	Bundle getBundle();

	String getMimeType(String file);

	String getName();

	String getCharsetEncoding();

	boolean isTraceAccess();

	boolean isTraceErrors();

	Date getUpdated();

	File findFile(String path);

    boolean prepareHead(CallContext context, String type);

}
