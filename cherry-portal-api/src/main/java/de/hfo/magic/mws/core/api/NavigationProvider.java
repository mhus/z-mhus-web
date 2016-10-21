package de.hfo.magic.mws.core.api;

import de.mhus.lib.cao.CaoNode;

public interface NavigationProvider {

	String RESOURCE_ID = "resource_id";

	public CaoNode getNode(String path);
	
}
