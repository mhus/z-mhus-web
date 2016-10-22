package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.RendererResolver;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.errors.NotFoundException;

public class DefaultRendererResolver implements RendererResolver {

	@Override
	public ResourceRenderer getRenderer(VirtualHost vHost, CaoNode resource, String httpMethod, String[] selectors, String retType) {
		
		httpMethod = httpMethod.toLowerCase();
		
		String renderReference = null;
		ResourceRenderer renderer = null;
		
		renderReference = resource.getString("render_reference_" + httpMethod + "_" + retType, null);
		if (renderReference != null) {
			renderer = vHost.getRenderer(renderReference);
			if (renderer != null) 
				return renderer;
			else
				throw new NotFoundException();
		}
		renderReference = resource.getString("render_reference_" + httpMethod, null);
		if (renderReference != null) {
			renderer = vHost.getRenderer(renderReference);
			if (renderer != null) 
				return renderer;
			else
				throw new NotFoundException();
		}
		
		renderer = vHost.getRenderer(httpMethod + "_" + retType);
		if (renderer != null) return renderer;
		
		renderer = vHost.getRenderer(httpMethod);
		if (renderer != null) return renderer;
		
		return renderer;
	}

}
