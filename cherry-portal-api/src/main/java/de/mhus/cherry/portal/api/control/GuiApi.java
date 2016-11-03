package de.mhus.cherry.portal.api.control;

import javax.security.auth.Subject;

import de.mhus.lib.core.IProperties;

public interface GuiApi {

	boolean hasAccess(String role);
	
	boolean openSpace(String spaceId, String subSpace, String search);
	
	Subject getCurrentUser();
	
	String getHost();
}
