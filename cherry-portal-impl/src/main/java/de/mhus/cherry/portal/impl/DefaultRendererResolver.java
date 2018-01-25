package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.RendererResolver;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.errors.NotFoundException;

public class DefaultRendererResolver implements RendererResolver {

	@Override
	public ResourceRenderer getRenderer(CallContext call) throws NotFoundException {
		
		String httpMethod = call.getHttpMethod(); // already lower case
		
		String renderReference = null;
		ResourceRenderer renderer = null;
		
		renderReference = call.getResource().getString("render_reference_" + httpMethod + "_" + call.getReturnType(), null);
		if (renderReference != null) {
			renderer = call.getVirtualHost().getResourceRenderer(renderReference);
			if (renderer != null) 
				return renderer;
			else
				throw new NotFoundException();
		}
		renderReference = call.getResource().getString("render_reference_" + httpMethod, null);
		if (renderReference != null) {
			renderer = call.getVirtualHost().getResourceRenderer(renderReference);
			if (renderer != null) 
				return renderer;
			else
				throw new NotFoundException();
		}
		
		renderer = call.getVirtualHost().getResourceRenderer(httpMethod + "_" + call.getReturnType());
		if (renderer != null) return renderer;
		
		renderer = call.getVirtualHost().getResourceRenderer(httpMethod);
		if (renderer != null) return renderer;
		
		return renderer;
	}

}
