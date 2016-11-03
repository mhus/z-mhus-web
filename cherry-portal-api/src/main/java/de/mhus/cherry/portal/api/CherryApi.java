package de.mhus.cherry.portal.api;

import javax.servlet.ServletRequest;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.osgi.sop.api.SApi;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface CherryApi extends SApi {
	
	//final String SESSION_USER_OBJECT = "cherry_user_object";
	final String SESSION_ACCESS_NAME = "cherry_access";

	VirtualHost findVirtualHost(String host);
	
	FileDeployer findFileDeployer(String suffix);

	String getMimeType(String file);

	DeployDescriptor getDeployDescritor(String symbolicName);

	/**
	 * Looks for the attribute and iterate recursive to the root.
	 * The lookup can be cached by the api.
	 * 
	 * @param resource resource instance
	 * @param name name of the key to find
	 * @return
	 */
	String getRecursiveString(CaoNode resource, String name);

//	IProperties getCherrySession(RequestWrapper request);
	IProperties getCherrySession(String sessionId);
	
	AaaContext getContext(String sessionId);

	boolean canEditResource(CallContext call, CaoNode res);

}
