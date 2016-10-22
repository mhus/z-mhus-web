package de.mhus.cherry.portal.impl;

import java.io.File;

import de.mhus.lib.cao.fs.FsConnection;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		DefaultNavigationProvider nv = new DefaultNavigationProvider();
		nv.setConnection(new FsConnection(new File("cherry/demo/webcontent/nav"), true, false));
		setNavigationProvider(nv);
		
		setRendererResolver(new DefaultRendererResolver());
		setResourceResolver(new DefaultResourceResolver());
		
		addResourceprovider("default", new DefaultResourceProvider(new FsConnection(new File("cherry/demo/webcontent/res"), true, false) ) );
	}
}
