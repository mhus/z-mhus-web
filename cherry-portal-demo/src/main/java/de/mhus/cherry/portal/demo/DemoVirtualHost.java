package de.mhus.cherry.portal.demo;

import java.io.File;

import de.mhus.cherry.portal.impl.DefaultNavigationProvider;
import de.mhus.cherry.portal.impl.DefaultRendererResolver;
import de.mhus.cherry.portal.impl.DefaultResourceProvider;
import de.mhus.cherry.portal.impl.DefaultResourceResolver;
import de.mhus.cherry.portal.impl.DefaultVirtualHost;
import de.mhus.lib.cao.fs.FsConnection;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		DefaultNavigationProvider nv = new DefaultNavigationProvider();
		nv.setConnection(new FsConnection(new File("cherry/private/cherry-portal-demo/webcontent/nav"), true, false));
		setNavigationProvider(nv);
		
		setRendererResolver(new DefaultRendererResolver());
		setResourceResolver(new DefaultResourceResolver());
		
		addResourceprovider("default", new DefaultResourceProvider(new FsConnection(new File("cherry/private/cherry-portal-demo/webcontent/res"), true, false) ) );
	}
}
