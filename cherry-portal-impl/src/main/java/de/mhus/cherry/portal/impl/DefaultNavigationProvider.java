package de.mhus.cherry.portal.impl;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CherryUtil;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.ResourceProvider;
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
