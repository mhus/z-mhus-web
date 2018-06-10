package de.mhus.cherry.web.api;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface WebArea {

	void doInitialize(VirtualHost vHost, IConfig config) throws MException;

	/**
	 * Return true if the area was able to consume the call.
	 * 
	 * @param call
	 * @return true if consumed
	 * @throws MException
	 */
	public boolean doRequest(CallContext call) throws MException;

}
