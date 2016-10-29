package de.mhus.cherry.portal.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.EditorFactory;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.RendererResolver;
import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ResourceResolver;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;

public class DefaultVirtualHost extends MLog implements VirtualHost {

	private NavigationProvider navigationProvider;
	private ResourceResolver resourceResolver;
	private RendererResolver rendererResolver;
	private HashMap<String, ResourceProvider> localResourceProvider = new HashMap<>();

	public DefaultVirtualHost() {
	}
	
	@Override
	public void sendError(CallContext call, int sc) {
		try {
			if (!call.getHttpResponse().isCommitted())
				call.getHttpResponse().sendError(sc);
			else {
				//TODO send error in content ...
				call.getHttpResponse().getOutputStream().print("An Error Occured !" );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	@Override
	public void processRequest(CallContext iCall) {
		CherryCallContext call = (CherryCallContext)iCall;
		
		try {
			String subPath = "";
			String control = "";
			String subType = ""; // type of path after nav node, nav/.content/res/res/
			String[] selectors;
			String retType = "";
			String path = call.getHttpPath();
			boolean isFolder = false;
			if (MString.isIndex(path, '.')) {
				control = MString.afterIndex(path, '.');
				path = MString.beforeIndex(path, '.');
			}
			
			if (path.endsWith("/")) {
				isFolder = true;
				path = path.substring(0, path.length()-1);
			}
			
			if (MString.isIndex(control, '/')) {
				subPath = MString.afterIndex(control, '/');
				control = MString.beforeIndex(control, '/');
			}
			
			// if folder, the first part is the type of sub resource
			if (isFolder) {
				if (MString.isIndex(control, '.')) {
					subType = MString.beforeIndex(control, '.');
					control = MString.afterIndex(control, '.');
				} else {
					subType = control;
					control = "";
				}
			}
			// last one is return type
			if (MString.isIndex(control, '.')) {
				retType = MString.afterLastIndex(control, '.').toLowerCase();
				control = MString.beforeLastIndex(control, '.');
			} else {
				retType = control;
				control = "";
			}
			// the rest are selectors
			if (MString.isSet(control))
				selectors = control.split("\\.");
			else
				selectors = new String[0];
			
			
			String resId = call.getNavigationResource().getString(NavigationProvider.RESOURCE_ID);
			if (resId == null) {
				log().d("resource id not found", call);
				sendError(call, HttpServletResponse.SC_NOT_FOUND);
			}
			CaoNode resResource = getResourceResolver().getResourceById(this, resId);
			if (resResource == null) {
				log().d("resource not found", call, resId);
				sendError(call, HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			call.setMainResource(resResource);
			call.setSelectors(selectors);
			
			String resName = resResource.getName();
			if (MString.isEmpty(retType) && resName != null && MString.isIndex(resName, '.') )
				retType = MString.afterLastIndex(resName, '.');
			call.setReturnType(retType);
			
			if (isFolder && MString.isSet(subType)) {
				
				if ("content".equals(subType)) {
					
					for (String part : subPath.split("/")) {
						if (MString.isSetTrim(part)) {
							resResource = (CaoNode) resResource.getNode(part);
							if (resResource == null) {
								log().d("sub resource not found", call, subPath);
								sendError(call, HttpServletResponse.SC_NOT_FOUND);
								return;
							}
						}
					}
					
				}
				
			}
			call.setResource(resResource);

			getRendererResolver().getRenderer(iCall).doRender(iCall);
			
			
		} catch (NotFoundException t) {
			log().d("not found",call,t);
			sendError(call, HttpServletResponse.SC_NOT_FOUND);
		} catch (Throwable t) {
			UUID id = UUID.randomUUID();
			log().w("internal error", id,call,t);
			sendError(call, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
		name = name.toLowerCase();
		ResourceProvider provider = localResourceProvider.get(name);
		if (provider == null)
			try {
				provider = MOsgi.getService(ResourceProvider.class, MOsgi.filterServiceName("cherry_resource_" + name));
			} catch (NotFoundException e) {}
		return provider;
	}

	public void setResourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	@Override
	public RendererResolver getRendererResolver() {
		return rendererResolver;
	}

	public void setRendererResolver(RendererResolver rendererResolver) {
		this.rendererResolver = rendererResolver;
	}

	@Override
	public ResourceRenderer getResourceRenderer(String name) {
		name = name.toLowerCase();
		ResourceRenderer renderer = null;
		try {
			renderer = MOsgi.getService(ResourceRenderer.class, MOsgi.filterServiceName("cherry_renderer_" + name));
		} catch (NotFoundException e) {}
		return renderer;
	}

	public void addResourceprovider(String name, ResourceProvider provider) {
		localResourceProvider.put(name, provider);
	}

	@Override
	public String getDefaultContentType() {
		return "text/html";
	}

	@Override
	public EditorFactory getFactory(String name) {
		name = name.toLowerCase();
		EditorFactory factory = null;
		try {
			factory = MOsgi.getService(EditorFactory.class, MOsgi.filterServiceName("cherry_editor_" + name));
		} catch (NotFoundException e) {}
		return factory;
	}

	@Override
	public ScriptRenderer getScriptRenderer(String name) {
		name = name.toLowerCase();
		ScriptRenderer renderer = null;
		try {
			renderer = MOsgi.getService(ScriptRenderer.class, MOsgi.filterServiceName("cherry_script_renderer_" + name));
		} catch (NotFoundException e) {}
		return renderer;
	}
	
}
