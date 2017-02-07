package de.mhus.cherry.portal.api;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MEventHandler;
import de.mhus.lib.core.security.AccountSource;
import de.mhus.lib.core.security.AuthorizationSource;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.lib.servlet.ResponseWrapper;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.action.ActionDescriptor;

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


	/**
	 * Return a list ov available EditorFactories for the given Host.
	 * The list is a combination of deployed and configured page types.
	 * @param nav
	 * @return 
	 */
	public Collection<EditorFactory> getAvailablePageTypes(CaoNode nav);

	/**
	 * Returns a list of actions from ActionApi with the given type.
	 * The list is filtered and ordered for the current virtual host using
	 * the corresponding configuration list.
	 * 
	 * @param type
	 * @param node
	 * @return 
	 */
	public Collection<ActionDescriptor> getActions(String type, CaoNode[] node);
	
	public MEventHandler<StructureChangesListener> getStructureRegistry();

	void doUpdates();

	public void doPrepareCreatedWidget(CaoNode res, EditorFactory factory);

}
