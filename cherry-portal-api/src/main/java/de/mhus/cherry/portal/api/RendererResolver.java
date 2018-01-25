package de.mhus.cherry.portal.api;

import de.mhus.lib.errors.NotFoundException;

public interface RendererResolver {

	ResourceRenderer getRenderer(CallContext call) throws NotFoundException;
	
}
