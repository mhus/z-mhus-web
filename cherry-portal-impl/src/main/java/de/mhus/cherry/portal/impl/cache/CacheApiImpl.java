package de.mhus.cherry.portal.impl.cache;

import java.util.WeakHashMap;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CacheApi;
import de.mhus.cherry.portal.api.Container;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

@Component
public class CacheApiImpl extends MLog implements CacheApi {

	WeakHashMap<String, Container> cache = new WeakHashMap<>();
	private boolean enabled = true;
	static CacheApiImpl instance;
	
    @Activate
    public void activate(ComponentContext ctx) {
    	instance = this;
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	instance = null;
    }
    
	@Override
	public Container get(CaoNode node, String name) {
		if (!enabled) return null;
		try {
			String key = getName(node, name);
			if (key == null) return null;
			Container cont = cache.get( key );
			if (cont == null) return null;
			if (cont.isTimeout()) {
				cache.remove(key);
				return null;
			}
			return cont;
		} catch (MException e) {
			log().d(e);
		}
		return null;
	}

	public String getName(CaoNode node, String name) throws MException {
		if (node == null) return null;
		String source = node.getConnection().getName();
		String id = node.getId();
		return source + "|" + id + "|" + name;
	}

	@Override
	public void put(CaoNode node, String name, Object value) {
		if (!enabled) return;
		try {
			String key = getName(node, name);
			if (key == null) return;
			cache.put(key, new Container(value) );
		} catch (MException e) {
			log().d(e);
		}
		return;
	}
	
	public void clear() {
		cache.clear();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) clear();
	}

	public int size() {
		return cache.size();
	}

	@Override
	public String getString(CaoNode node, String name) {
		Container ret = get(node, name);
		if (ret == null) return null;
		return ret.getString();
	}
	
}
