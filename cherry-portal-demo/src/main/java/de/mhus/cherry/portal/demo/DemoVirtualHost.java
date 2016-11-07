package de.mhus.cherry.portal.demo;

import java.io.File;

import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.impl.DefaultNavigationProvider;
import de.mhus.cherry.portal.impl.DefaultRendererResolver;
import de.mhus.cherry.portal.impl.DefaultResourceProvider;
import de.mhus.cherry.portal.impl.DefaultResourceResolver;
import de.mhus.cherry.portal.impl.DefaultVirtualHost;
import de.mhus.cherry.portal.impl.api.DefaultBaseApi;
import de.mhus.lib.cao.auth.AuthConnection;
import de.mhus.lib.cao.fs.FsConnection;
import de.mhus.lib.cao.fsdb.FdConnection;
import de.mhus.lib.cao.util.SharedDataSource;
import de.mhus.osgi.sop.api.Sop;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		
		try {
			CherryApi api = Sop.getApi(CherryApi.class);
			DeployDescriptor privDep = api.getDeployDescritor(FrameworkUtil.getBundle(DemoVirtualHost.class));
			File priv = privDep.getPath(SPACE.PRIVATE);
			DefaultNavigationProvider nv = new DefaultNavigationProvider();
			nv.setConnection(new AuthConnection( new FsConnection("navigation", new File(priv, "webcontent/nav"), true, false), new DemoAuth() ) );
			setNavigationProvider( nv );
			
			setRendererResolver(new DefaultRendererResolver());
			setResourceResolver(new DefaultResourceResolver());
			
			addResourceDataSource(new SharedDataSource(new AuthConnection( new FdConnection("default", new File(priv, "webcontent/res"), false), new DemoAuth() ) ) );
			addResourceDataSource(new SharedDataSource(new AuthConnection( new FsConnection("pub", new File(priv, "webcontent/pub"), true, false), null ) ) );
			
			addApiProvider("base", new DefaultBaseApi());
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
