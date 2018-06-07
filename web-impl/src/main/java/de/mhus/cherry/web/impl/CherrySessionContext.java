package de.mhus.cherry.web.impl;

import de.mhus.cherry.web.api.SessionContext;
import de.mhus.lib.core.MProperties;

public class CherrySessionContext extends MProperties implements SessionContext {

	private String sessionId;
	
	public CherrySessionContext(String sessionId) {
		this.sessionId = sessionId;
	}
	@Override
	public String getSessionId() {
		return sessionId;
	}

}
