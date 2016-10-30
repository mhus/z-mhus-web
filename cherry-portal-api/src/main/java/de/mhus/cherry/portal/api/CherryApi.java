package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.SApi;

public interface CherryApi extends SApi {
	
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

}
