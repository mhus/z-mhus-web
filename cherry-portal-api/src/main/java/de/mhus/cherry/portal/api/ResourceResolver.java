package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface ResourceResolver {

	CaoNode getResource(VirtualHost vHost, String resId);
	
}
