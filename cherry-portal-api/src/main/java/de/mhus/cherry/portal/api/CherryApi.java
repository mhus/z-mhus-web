package de.mhus.cherry.portal.api;

import de.mhus.osgi.sop.api.SApi;

public interface CherryApi extends SApi {
	
	VirtualHost findVirtualHost(String host);
	
	FileDeployer findFileDeployer(String suffix);

	String getMimeType(String file);

	DeployDescriptor getDeployDescritor(String symbolicName);

}
