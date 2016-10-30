package de.mhus.cherry.portal.demo;

import java.io.File;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.impl.DefaultNavigationProvider;
import de.mhus.cherry.portal.impl.DefaultRendererResolver;
import de.mhus.cherry.portal.impl.DefaultResourceProvider;
import de.mhus.cherry.portal.impl.DefaultResourceResolver;
import de.mhus.cherry.portal.impl.DefaultVirtualHost;
import de.mhus.lib.cao.fs.FsConnection;
import de.mhus.lib.core.MThread;
import de.mhus.osgi.sop.api.Sop;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		
		CherryApi api = Sop.getApi(CherryApi.class);
		DeployDescriptor privDep = api.getDeployDescritor("cherry-portal-demo");
		File priv = privDep.getPath(SPACE.PRIVATE);
		DefaultNavigationProvider nv = new DefaultNavigationProvider();
		nv.setConnection(new FsConnection("navigation", new File(priv, "webcontent/nav"), true, false));
		setNavigationProvider(nv);
		
		setRendererResolver(new DefaultRendererResolver());
		setResourceResolver(new DefaultResourceResolver());
		
		addResourceprovider(new DefaultResourceProvider(new FsConnection("default", new File(priv, "webcontent/res"), true, false) ) );
	}
}
