package de.mhus.cherry.portal.api;

import java.io.IOException;

import de.mhus.lib.cao.CaoNode;

public interface VirtualHost {

	String RESOURCE_ID = "resource_id";

	public NavigationProvider getNavigationProvider();

	public void sendError(CallContext context, int sc) throws IOException;

	public void processRequest(CallContext context);

	public ResourceResolver getResourceResolver();
	
	public ResourceProvider getResourceProvider(String name);
	
	public RendererResolver getRendererResolver();

	public ResourceRenderer getResourceRenderer(String name);

	public String getDefaultContentType();
	
	public ScriptRenderer getScriptRenderer(String name);

	public EditorFactory getControlEditorFactory(String name);

	/**
	 * Insert the path like using in the browser and get the specified resource (not navigation node)
	 * 
	 * @param navPath The path to search for
	 * @return The referenced resource
	 */
	public CaoNode getNavigationResource(String navPath);
	
}
