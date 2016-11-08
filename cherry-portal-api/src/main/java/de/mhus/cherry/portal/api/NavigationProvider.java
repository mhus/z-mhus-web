package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface NavigationProvider {

	public CaoNode getNode(String path);
	
}
