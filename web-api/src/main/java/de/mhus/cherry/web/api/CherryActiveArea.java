package de.mhus.cherry.web.api;

import de.mhus.lib.errors.MException;

public interface CherryActiveArea {

	public void doRequest(CallContext context) throws MException;

}
