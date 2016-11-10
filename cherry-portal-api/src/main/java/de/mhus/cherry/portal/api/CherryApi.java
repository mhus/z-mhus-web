package de.mhus.cherry.portal.api;

import javax.servlet.ServletRequest;

import org.osgi.framework.Bundle;

import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.osgi.sop.api.SApi;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface CherryApi extends SApi {
	
	public static final String RESOURCE_ID = "cherry:resource_id";
	public static final String DEFAULT_RESOURCE_PROVIDER = "default";
	public static final String NAV_HIDDEN = "cherry:hidden";
	public static final String NAV_TITLE = "title";
	public static final String NAV_CONTENT_NODE = "_content";
	public static final String RES_RET_TYPE = "cherry:retType";
	public static final String RET_TYPE_PAGE = "page";
	public static final String DEFAULT_NAVIGATION_PROVIDER = "navigation";

	VirtualHost findVirtualHost(String host);
	
	FileDeployer findFileDeployer(String suffix);

	String getMimeType(String file);

	DeployDescriptor getDeployDescritor(Bundle bundle);

	/**
	 * Looks for the attribute and iterate recursive to the root.
	 * The lookup can be cached by the api.
	 * 
	 * @param resource resource instance
	 * @param name name of the key to find
	 * @return
	 */
	String getRecursiveString(CaoNode resource, String name);

	boolean canEditResource(CallContext call, CaoNode res);

	CallContext getCurrentCall();

	boolean deleteNavNode(CaoNode nav);

	NavNode createNavNode(VirtualHost vHost, CaoNode parent, String name, String title) throws CaoException;
	

}
