package de.hfo.magic.mws.core.impl;

import de.hfo.magic.mws.core.api.ResourceProvider;
import de.hfo.magic.mws.core.api.VirtualHost;
import de.hfo.magic.mws.core.api.ResourceResolver;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;

public class DefaultResourceResolver implements ResourceResolver {

	@Override
	public CaoNode getResourceById(VirtualHost vHost, String resId) {
		String provName = "default";
		if (MString.isIndex(resId, ':')) {
			provName = MString.beforeIndex(resId, ':');
			resId = MString.afterIndex(resId, ':');
		}
		ResourceProvider resProvider = vHost.getResourceProvider(provName);
		if (resProvider == null) return null; //TODO throw exception?
		CaoNode res = resProvider.getResourceById(resId);
		return res;
	}

	@Override
	public CaoNode getResourceByPath(VirtualHost vHost, String path) {
		String provName = "default";
		if (MString.isIndex(path, ':')) {
			provName = MString.beforeIndex(path, ':');
			path = MString.afterIndex(path, ':');
		}
		ResourceProvider resProvider = vHost.getResourceProvider(provName);
		if (resProvider == null) return null; //TODO throw exception?
		CaoNode res = resProvider.getResourceByPath(path);
		return res;
	}
	
}
