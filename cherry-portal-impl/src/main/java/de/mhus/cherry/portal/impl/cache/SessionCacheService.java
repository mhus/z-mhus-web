package de.mhus.cherry.portal.impl.cache;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.impl.InternalCherryApiImpl;
import de.mhus.osgi.services.AbstractCacheControl;
import de.mhus.osgi.services.CacheControlIfc;

@Component(provide=CacheControlIfc.class)
public class SessionCacheService extends AbstractCacheControl {

	{
		supportDisable = false;
	}

	@Override
	public long getSize() {
		return InternalCherryApiImpl.instance.globalSession.size();
	}

	@Override
	public void clear() {
		InternalCherryApiImpl.instance.globalSession.clear();
	}

}
