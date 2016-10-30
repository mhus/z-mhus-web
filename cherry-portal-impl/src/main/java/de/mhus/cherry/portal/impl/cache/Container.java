package de.mhus.cherry.portal.impl.cache;

import de.mhus.lib.core.MTimeInterval;

public class Container {

;
	public Object object;
	public long timestamp = System.currentTimeMillis();
	
	public Container(Object value) {
		object = value;
	}

	public boolean isTimeout() {
		
		return System.currentTimeMillis() - timestamp > CacheApiImpl.timeout;
	}

}
