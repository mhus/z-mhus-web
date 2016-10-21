package de.hfo.magic.mws.core.impl;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hfo.magic.mws.core.api.NavigationProvider;
import de.hfo.magic.mws.core.api.ResourceProvider;
import de.hfo.magic.mws.core.api.CherryUtil;
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
