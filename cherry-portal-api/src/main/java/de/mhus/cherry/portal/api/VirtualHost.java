package de.mhus.cherry.portal.api;

import java.io.IOException;

public interface VirtualHost {

	String RESOURCE_ID = "resource_id";

	public NavigationProvider getNavigationProvider();

	public void sendError(CallContext context, int sc) throws IOException;

	public void processRequest(CallContext context);

	public ResourceResolver getResourceResolver();
	
	public ResourceProvider getResourceProvider(String name);
	
	public RendererResolver getRendererResolver();

	public ResourceRenderer getRenderer(String name);

	public String getDefaultContentType();
	
	public EditorFactory getFactory(String name);
	
}
