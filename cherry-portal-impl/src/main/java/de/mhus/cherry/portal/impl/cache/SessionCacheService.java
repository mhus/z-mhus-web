package de.mhus.cherry.portal.impl.cache;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.impl.InternalCherryApiImpl;
import de.mhus.lib.karaf.services.CacheControlIfc;

@Component
public class SessionCacheService implements CacheControlIfc {

	@Override
	public long getSize() {
		return InternalCherryApiImpl.instance.globalSession.size();
	}

	@Override
	public String getName() {
		return "de.mhus.cherry.portal.impl.sessionCache";
	}

	@Override
	public void clear() {
		InternalCherryApiImpl.instance.globalSession.clear();
	}

}
