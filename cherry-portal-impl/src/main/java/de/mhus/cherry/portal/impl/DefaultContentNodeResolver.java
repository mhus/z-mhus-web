package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ContentNodeResolver;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;

public class DefaultContentNodeResolver implements ContentNodeResolver {

	private VirtualHost vHost;

	public DefaultContentNodeResolver(VirtualHost vHost) {
		this.vHost = vHost;
	}
	
	@Override
	public CaoNode doResolve(CaoNode nav) {
		String resId = nav.getString(CherryApi.RESOURCE_ID, null);
		if (MString.isSet(resId)) {
			return vHost.getResourceResolver().getResource(vHost, resId);
		} else {
			return nav.getNode(CherryApi.NAV_CONTENT_NODE);
		}
	}

}
