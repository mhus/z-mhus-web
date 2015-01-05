package de.mhus.osgi.cherry.impl.osgi;

import org.osgi.framework.FrameworkUtil;



//import org.osgi.service.component.ComponentContext;
//import aQute.bnd.annotation.component.Activate;
//import aQute.bnd.annotation.component.Deactivate;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.impl.DefaultApplication;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

public class AbstractOsgiBundleApplication extends DefaultApplication {
/*
	@Activate
	public void doActivate(ComponentContext ctx) {
		super.doActivate(ctx);
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		super.doDeactivate(ctx);
	}
*/
	
	private BundleResourceRoot root;

	@Override
	public void configureHost(VirtualHost host, ResourceNode config) throws Exception {
		root = new BundleResourceRoot(FrameworkUtil.getBundle(getClass()), "/webcontent/");
		super.configureHost(host, config);
	}

	@Override
	public ResourceNode getResource(VirtualHost host, String target) {
		ResourceNode res = ((DefaultVirtualHost)host).getDocumentRootResource(target);
		if (res != null) return res;
		res = root.getResource(target);
		return res;
	}
	
}
