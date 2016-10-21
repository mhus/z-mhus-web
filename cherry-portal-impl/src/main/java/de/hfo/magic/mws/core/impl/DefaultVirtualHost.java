package de.hfo.magic.mws.core.impl;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hfo.magic.mws.core.api.NavigationProvider;
import de.hfo.magic.mws.core.api.ResourceProvider;
import de.hfo.magic.mws.core.api.CherryUtil;
import de.hfo.magic.mws.core.api.VirtualHost;
import de.hfo.magic.mws.core.api.ResourceResolver;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.karaf.MOsgi;

public class DefaultVirtualHost extends MLog implements VirtualHost {

	private NavigationProvider navigationProvider;
	private ResourceResolver resourceResolver;

	public DefaultVirtualHost() {
	}
	
	@Override
	public void sendError(HttpServletResponse res, String path, int sc) {
		try {
			res.sendError(sc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	@Override
	public void processRequest(HttpServletRequest req, HttpServletResponse res, CaoNode navResource) {

		try {
			String subPath = "";
			String path = req.getPathInfo();
			boolean isFolder = false;
			if (MString.isIndex(path, '.')) {
				subPath = MString.afterIndex(path, '.');
				path = MString.beforeIndex(path, '.');
			}
			
			if (path.endsWith("/")) {
				isFolder = true;
				path = path.substring(0, path.length()-1);
			}
			
			if (isFolder) {
				
			} else {
				String resId = navResource.getString(NavigationProvider.RESOURCE_ID);
				CaoNode resResource = getResourceResolver().getResourceById(this, resId);
				if (resResource == null) {
					sendError(res, path, HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
//				processResource(req, res, )
				
				
			}
		} catch (Throwable t) {
			UUID id = UUID.randomUUID();
			log().w(id,t);
			sendError(res, id.toString(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public NavigationProvider getNavigationProvider() {
		return navigationProvider;
	}

	public void setNavigationProvider(NavigationProvider navigationProvider) {
		this.navigationProvider = navigationProvider;
	}

	@Override
	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	@Override
	public ResourceProvider getResourceProvider(String name) {
		ResourceProvider provider = MOsgi.getService(ResourceProvider.class, MOsgi.filterServiceName("cherry_resource_" + name));
		return provider;
	}

	public void setResourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

}
