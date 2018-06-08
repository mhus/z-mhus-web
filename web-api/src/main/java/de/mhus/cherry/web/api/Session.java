package de.mhus.cherry.web.api;

import de.mhus.lib.core.IProperties;

public interface Session extends IProperties {

	String getSessionId();
	
	/**
	 * Get public part of session
	 * @return public part
	 */
	IProperties pub();
	
}
