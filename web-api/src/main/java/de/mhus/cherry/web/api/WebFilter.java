package de.mhus.cherry.web.api;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface WebFilter {

	void doInitialize(VirtualHost vHost, IConfig config) throws MException;
	
	/**
	 * Return true if the page can be displayed. false will
	 * prevent processing of the page
	 * @param call
	 * @return true if ok
	 * @throws MException 
	 */
	boolean doFilterBegin(InternalCallContext call) throws MException;

	void doFilterEnd(InternalCallContext call) throws MException;

}
