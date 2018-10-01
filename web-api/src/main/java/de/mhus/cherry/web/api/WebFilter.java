package de.mhus.cherry.web.api;

import java.util.UUID;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface WebFilter {

	void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException;
	
	/**
	 * Return true if the page can be displayed. false will
	 * prevent processing of the page
	 * @param instance to identify the instance in stateless environments like services
	 * @param call
	 * @return true if ok
	 * @throws MException 
	 */
	boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException;

	void doFilterEnd(UUID instance, InternalCallContext call) throws MException;

}
