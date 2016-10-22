package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;

public interface RendererResolver {

	ResourceRenderer getRenderer(VirtualHost vHost, CaoNode resource, String httpMethod, String[] selectors, String retType);
	
}
