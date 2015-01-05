package de.mhus.osgi.cherry.api.central;

import java.util.Properties;

public interface CentralRequestHandlerAdmin {

	void updateCentralHandlers(Properties rules);
	CentralRequestHandler[] getCentralHandlers();
	Properties getCentralHandlerProperties();

}
