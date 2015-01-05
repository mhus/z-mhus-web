package de.mhus.osgi.tutorial.websampleapp;

import java.util.HashMap;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.impl.DefaultApplicationContext;

public class SampleContext extends DefaultApplicationContext {

	public SampleContext() {}
	public SampleContext(SampleApp sampleApp, VirtualHost host,
			ResourceNode config) throws Exception {
		doActivate(sampleApp, host, config);
	}

	public void doActivate(VirtualApplication defaultApplication,
			VirtualHost host, ResourceNode config) throws Exception {
		super.doActivate(defaultApplication, host, config);
		resources.put("/_resource/callback", new SampleCallback(this) );
	}
	
}
