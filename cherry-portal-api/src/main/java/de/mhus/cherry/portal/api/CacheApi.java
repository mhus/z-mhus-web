package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.SApi;

public interface CacheApi extends SApi {

	Container get(CaoNode node, String name);
	String getString(CaoNode node, String name);
	void put(CaoNode node, String name, Object value);
	
}
