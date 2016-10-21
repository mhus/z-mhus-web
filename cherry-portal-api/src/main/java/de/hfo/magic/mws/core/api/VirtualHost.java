package de.hfo.magic.mws.core.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.cao.CaoNode;

public interface VirtualHost {

	String RESOURCE_ID = "resource_id";

	public NavigationProvider getNavigationProvider();

	public void sendError(HttpServletResponse res, String path, int sc) throws IOException;

	public void processRequest(HttpServletRequest req, HttpServletResponse res, CaoNode navResource);

	public ResourceResolver getResourceResolver();
	
	public ResourceProvider getResourceProvider(String name);
	
}
