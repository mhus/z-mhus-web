package de.mhus.cherry.web.api;

import de.mhus.lib.errors.MException;

public interface WebFilter {

	/**
	 * Return true if the page can be displayed. false will
	 * prevent processing of the page
	 * @param call
	 * @return true if ok
	 * @throws MException 
	 */
	public boolean doFilterBegin(InternalCallContext call) throws MException;

	public void doFilterEnd(InternalCallContext call) throws MException;

}
