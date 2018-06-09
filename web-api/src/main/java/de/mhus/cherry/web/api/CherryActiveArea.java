package de.mhus.cherry.web.api;

import de.mhus.lib.errors.MException;

public interface CherryActiveArea {

	/**
	 * Return true if the area was able to consume the call.
	 * 
	 * @param context
	 * @return true if consumed
	 * @throws MException
	 */
	public boolean doRequest(CallContext call) throws MException;

}
