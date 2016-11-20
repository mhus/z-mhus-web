package de.mhus.cherry.portal.api;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.security.AccountSource;
import de.mhus.lib.core.security.AuthorizationSource;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.lib.servlet.ResponseWrapper;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface VirtualHost {

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

	public AaaContext doLogin(RequestWrapper request, ResponseWrapper response);

	public void processApiRequest(CallContext context);
	
	public List<String> getConfigurationList(String name);
	
	public AccountSource getAccountSource();
	
	public AuthorizationSource getAuthorizationSource();

	public ContentNodeResolver getContentNodeResolver();

	public ActionCallback getActionCallback(String actionName);

	public Set<String> getConfigurationListName();

	public void setConfigurationList(String name, List<String> list);
	
}
