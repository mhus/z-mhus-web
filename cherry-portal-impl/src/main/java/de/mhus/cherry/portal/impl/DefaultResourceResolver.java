package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.cherry.portal.api.ResourceResolver;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;

public class DefaultResourceResolver implements ResourceResolver {

	@Override
	public CaoNode getResource(VirtualHost vHost, String resId) {
		String provName = "default";
		if (MString.isIndex(resId, ':')) {
			provName = MString.beforeIndex(resId, ':');
			resId = MString.afterIndex(resId, ':');
		}
		ResourceProvider resProvider = vHost.getResourceProvider(provName);
		if (resProvider == null) return null; //TODO throw exception?
		CaoNode res = resProvider.getResource(resId);
		return res;
	}
	
}
