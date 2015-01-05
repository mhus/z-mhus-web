package org.ops4j.pax.web.service.jetty.internal;

import java.util.Properties;

import de.mhus.osgi.cherry.api.central.CentralRequestHandler;
import de.mhus.osgi.cherry.api.central.CentralRequestHandlerAdmin;
import aQute.bnd.annotation.component.Component;

/**
 * Central Request Handler Admin Impl
 * 
 * @author mikehummel
 *
 */
@Component(provide=CentralRequestHandlerAdmin.class,immediate=true)
public class CrhaImpl implements CentralRequestHandlerAdmin {

	@Override
	public void updateCentralHandlers(Properties rules) {
		JettyServerWrapper.instance.updateCentralHandlers(rules);		
	}

	@Override
	public CentralRequestHandler[] getCentralHandlers() {
		return JettyServerWrapper.instance.getCentralHandlers();
	}

	@Override
	public Properties getCentralHandlerProperties() {
		return JettyServerWrapper.instance.getCentralHandlerProperties();
	}

}
