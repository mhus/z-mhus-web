package de.hfo.magic.mws.core.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hfo.magic.mws.core.api.CherryNavigationProvider;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;

public class NavigationProvider extends MLog implements CherryNavigationProvider {

	private CaoConnection connection;
	
	public NavigationProvider() {
	}
	
	@Override
	public CaoNode getNode(String path) {
		
		
		
		return connection.getResourceByPath(path);
	}

	@Override
	public void sendError(HttpServletResponse res, String path, int scNotFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRequest(HttpServletRequest req, HttpServletResponse res, CaoNode navResource) {
		// TODO Auto-generated method stub
		
	}

	public CaoConnection getConnection() {
		return connection;
	}

	public void setConnection(CaoConnection connection) {
		this.connection = connection;
	}

}
