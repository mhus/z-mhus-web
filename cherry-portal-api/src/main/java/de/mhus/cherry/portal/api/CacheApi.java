package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.SApi;

public interface CacheApi extends SApi {

	public Object get(CaoNode node, String name);
	public String getString(CaoNode node, String name);
	public void put(CaoNode node, String name, Object value);
	
}
