package de.mhus.cherry.portal.impl;

import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;

public class DefaultNavigationProvider extends MLog implements NavigationProvider {

	private CaoConnection connection;
	
	public DefaultNavigationProvider() {
	}
	
	@Override
	public CaoNode getNode(String path) {
		
		if (MString.isIndex(path, '.'))
			path = MString.beforeIndex(path, '.');
		
		return connection.getResourceByPath(path);
	}

	public CaoConnection getConnection() {
		return connection;
	}

	public void setConnection(CaoConnection connection) {
		this.connection = connection;
	}

}
