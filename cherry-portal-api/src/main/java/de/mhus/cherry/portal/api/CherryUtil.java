package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;

public class CherryUtil {

	public static VirtualHost findVirtualHost(String host) {
		VirtualHost provider = null;
		try {
			provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_" + host));
		} catch (NotFoundException e) {}
		if (provider == null) {
			try {
				provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_default"));
			} catch (NotFoundException e) {}
		}
		
		return provider;
	}

}
