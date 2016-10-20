package de.hfo.magic.mws.core.api;

import de.mhus.lib.karaf.MOsgi;

public class CherryUtil {

	public static CherryNavigationProvider findNavigationProvider(String host) {
		CherryNavigationProvider provider = MOsgi.getService(CherryNavigationProvider.class, MOsgi.filterServiceName("virtual_host_" + host));
		if (provider == null) {
			provider = MOsgi.getService(CherryNavigationProvider.class, MOsgi.filterServiceName("virtual_host_default"));
		}
		
		return provider;
	}

}
