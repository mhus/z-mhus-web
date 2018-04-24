package de.mhus.cherry.portal.impl.cache;

import aQute.bnd.annotation.component.Component;
import de.mhus.osgi.services.AbstractCacheControl;
import de.mhus.osgi.services.CacheControlIfc;

@Component(provide=CacheControlIfc.class)
public class NodeAttributeCacheService extends AbstractCacheControl {

	{
		supportDisable = false;
	}

	@Override
	public long getSize() {
		return CacheApiImpl.instance.cache.size();
	}

	@Override
	public void clear() {
		CacheApiImpl.instance.cache.clear();
	}

}
