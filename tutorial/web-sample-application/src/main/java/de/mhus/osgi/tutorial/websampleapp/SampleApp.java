package de.mhus.osgi.tutorial.websampleapp;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.ApplicationContext;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.impl.osgi.AbstractOsgiBundleApplication;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

@Component(provide=VirtualApplication.class,immediate=true,properties="name=sample",name="SampleApp")
public class SampleApp extends AbstractOsgiBundleApplication {

	@Activate
	public void doActivate(ComponentContext ctx) {
		super.doActivate(ctx);
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		super.doDeactivate(ctx);
	}

	protected ApplicationContext createApplicationContext(
			VirtualHost host, ResourceNode config) throws Exception {
		return new SampleContext(this, host, config);
	}
}
