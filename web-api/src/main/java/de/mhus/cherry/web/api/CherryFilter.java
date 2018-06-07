package de.mhus.cherry.web.api;

import de.mhus.lib.errors.MException;

public interface CherryFilter {

	/**
	 * Return true if the page can be displayed. false will
	 * prevent processing of the page
	 * @param context
	 * @return true if ok
	 * @throws MException 
	 */
	public boolean doFilterBegin(CallContext context) throws MException;

	public void doFilterEnd(CallContext context) throws MException;

}
