package de.mhus.cherry.web.impl;

import de.mhus.cherry.web.api.Session;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;

public class CherrySession extends MProperties implements Session {

	private String sessionId;
	private MProperties pub;
	
	public CherrySession(String sessionId) {
		this.sessionId = sessionId;
	}
	@Override
	public String getSessionId() {
		return sessionId;
	}
	@Override
	public synchronized IProperties pub() {
		if (pub == null)
			pub = new MProperties();
		return pub;
	}

}
