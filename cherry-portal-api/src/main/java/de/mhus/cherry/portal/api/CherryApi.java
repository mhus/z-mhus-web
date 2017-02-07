package de.mhus.cherry.portal.api;

import java.util.Map;

import org.osgi.framework.Bundle;

import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.SApi;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface CherryApi extends SApi {
	
	public static final String RESOURCE_ID = "cherry:resourceId";
	public static final String DEFAULT_RESOURCE_PROVIDER = "default";
	public static final String NAV_HIDDEN = "cherry:hidden";
	public static final String NAV_TITLE = "title";
	public static final String NAV_CONTENT_NODE = "_content";
	public static final String RES_RET_TYPE = "cherry:retType";
	public static final String RET_TYPE_PAGE = "page";
	public static final String DEFAULT_NAVIGATION_PROVIDER = "navigation";
	public static final String NAV_CONTENT_NODE_PREFIX = "_";
	public static final String ACL_READ =  "read";
	public static final String ACL_WRITE =  "write";
	public static final String ACL_CREATE =  "create";
	public static final String ACL_DELETE =  "delete";
	public static final String ACL_VERSION =  "version";
	public static final String ACL_EXECUTE =  "execute";
	public static final String ACL_STRUCTURE = "structure";
	public static final String ACL_RENDITION = "rendition";
	public static final String ACL_PREFIX = "acl:";
	public static final String REFERENCE_ID = "cherry:reference";
	public static final String DATA_PREFIX =  "data:";
	public static final String REF_DATA = "data";
	public static final String REF_CONTENT = "content";
	public static final String REF_RES = "res";
	public static final String CONFIG_HOST_ALLOWED = "servlet_host_allowed";
	public static final String USER_ACCOUNT_TRAIL_ENABLED = "control:trailEnabled";
	public static final String PAGE_ALTERNATIVES_LIST = "content_page_alternatives";
	public static final String ACTION_MODIFY = "modify";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_CREATE = "create";

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

	NavNode createNavNode(VirtualHost vHost, CaoNode parent, String pageRendition, String name, String title) throws CaoException;

	/**
	 * Lookup for node acl defintion. 
	 * 
	 * @param node
	 * @param aclName The name of the requested acl
	 * @return
	 */
	boolean hasResourceAccess(CaoNode node, String aclName);
	
	boolean hasResourceAccess(AaaContext context, CaoNode node, String aclName);

	Map<String, Acl> getEffectiveAcls(CaoNode node);

	CaoNode getAclDefiningNode(CaoNode node, String aclName);

}
