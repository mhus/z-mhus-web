package de.hfo.magic.mws.core.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.cao.CaoNode;

public interface CherryNavigationProvider {

	public CaoNode getNode(String path);

	public void sendError(HttpServletResponse res, String path, int scNotFound);

	public void processRequest(HttpServletRequest req, HttpServletResponse res, CaoNode navResource);
	
}
