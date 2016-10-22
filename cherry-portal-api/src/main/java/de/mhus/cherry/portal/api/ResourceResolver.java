package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface ResourceResolver {

	CaoNode getResourceById(VirtualHost vHost, String resId);

	CaoNode getResourceByPath(VirtualHost vHost, String path);
	
}
