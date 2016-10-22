package de.mhus.cherry.portal.api;

public interface RendererResolver {

	ResourceRenderer getRenderer(CallContext call);
	
}
