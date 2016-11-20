package de.mhus.cherry.portal.impl.cache;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.karaf.services.CacheControlIfc;

@Component
public class NodeAttributeCacheService implements CacheControlIfc {

	@Override
	public long getSize() {
		return CacheApiImpl.instance.cache.size();
	}

	@Override
	public String getName() {
		return "de.mhus.cherry.portal.impl.nodeAttributeCache";
	}

	@Override
	public void clear() {
		CacheApiImpl.instance.cache.clear();
	}

}
