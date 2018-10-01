package de.mhus.cherry.web.api;

import java.util.UUID;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface WebArea {

	void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException;

	/**
	 * Return true if the area was able to consume the call.
	 * @param instance To identify the area in stateless environments
	 * 
	 * @param call
	 * @return true if consumed
	 * @throws MException
	 */
	public boolean doRequest(UUID instance, CallContext call) throws MException;

}
