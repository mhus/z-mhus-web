package de.hfo.magic.mws.core.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.karaf.MOsgi;

public class CherryUtil {

	public static VirtualHost findVirtualHost(String host) {
		VirtualHost provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("virtual_host_" + host));
		if (provider == null) {
			provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("virtual_host_default"));
		}
		
		return provider;
	}

}
