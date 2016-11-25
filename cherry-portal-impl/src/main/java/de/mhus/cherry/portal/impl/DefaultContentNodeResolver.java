package de.mhus.cherry.portal.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ContentNodeResolver;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.osgi.sop.api.Sop;

public class DefaultContentNodeResolver implements ContentNodeResolver {

	private VirtualHost vHost;

	public DefaultContentNodeResolver(VirtualHost vHost ) {
		this.vHost = vHost;
	}
	
	@Override
	public CaoNode doResolve(CaoNode nav) {
		String resId = nav.getString(CherryApi.RESOURCE_ID, null);
		CaoNode out = null;
		if (MString.isSet(resId)) {
			out = vHost.getResourceResolver().getResource(vHost, resId);
		} else {
			
			List<String> list = vHost.getConfigurationList(CherryApi.PAGE_ALTERNATIVES_LIST);
			if (list == null) {
				out = nav.getNode(CherryApi.NAV_CONTENT_NODE);
				if (out == null)
					out = nav.getNode("_public"); // as fallback a public node
			} else {
				for (String name : list) {
					int p = name.indexOf(':');
					if (p > 0 ) name = name.substring(0, p);
					if (name.startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX))
						out = nav.getNode(name);
					if (out != null) break;
 				}
			}
		}
		
		return out;
	}

	@Override
	public String getRecursiveString(NavNode nav, String name) {
		String suffix = nav.getMainRes().getName();
		CherryApi api = Sop.getApi(CherryApi.class);
		String value = api.getRecursiveString(nav.getNav(), name + "_" + suffix);
		if (MString.isSet(value)) return value;
		// fallback
		value = api.getRecursiveString(nav.getNav(), name);
		return value;
	}

	@Override
	public Map<String, String> getAlternatives(CaoNode nav, String name, String caption) {
		TreeMap<String, String> out = new TreeMap<>();
		out.put(name, caption);
		for (CaoNode node : nav.getNodes()) {
			String suffix = node.getName();
			if (suffix.startsWith( CherryApi.NAV_CONTENT_NODE_PREFIX)) {
				out.put(name + "_" + suffix , caption + " (" + suffix.substring(1) + ")");
			}
		}
		return out;
	}

	@Override
	public Map<String, String> getDefaultPages() {
		TreeMap<String, String> alternatives = new TreeMap<>();
		List<String> list = vHost.getConfigurationList(CherryApi.PAGE_ALTERNATIVES_LIST);
		if (list != null)
			for (String a : list) {
				if (MString.isIndex(a, ':') && a.startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX))
					alternatives.put(MString.beforeIndex(a, ':'), MString.afterIndex(a, ':'));
			}
		else {
			alternatives.put(CherryApi.NAV_CONTENT_NODE, "Default Page");
			alternatives.put(CherryApi.NAV_CONTENT_NODE_PREFIX + "public", "Public Page");
		}
			
		return alternatives;
	}

}
