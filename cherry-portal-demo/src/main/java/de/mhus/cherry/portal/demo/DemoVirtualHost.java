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
import de.mhus.cherry.portal.impl.aaa.DefaultAuthorizator;
import de.mhus.cherry.portal.impl.aaa.ReadAllAuthorizator;
import de.mhus.cherry.portal.impl.aaa.ResourceAccountSource;
import de.mhus.cherry.portal.impl.aaa.ResourceAuthorizationSource;
import de.mhus.cherry.portal.impl.api.DefaultBaseApi;
import de.mhus.lib.cao.auth.AuthConnection;
import de.mhus.lib.cao.fs.FsConnection;
import de.mhus.lib.cao.fsdb.FdConnection;
import de.mhus.lib.cao.util.SharedDataSource;
import de.mhus.lib.core.MThread;
import de.mhus.osgi.sop.api.Sop;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		
		MThread.asynchron(new Runnable(){
			
			@Override
			public void run() {
				Sop.waitForApi(CherryApi.class, 10000);
		
				try {
					CherryApi api = Sop.getApi(CherryApi.class);
					DeployDescriptor privDep = api.getDeployDescritor(FrameworkUtil.getBundle(DemoVirtualHost.class));
					File priv = privDep.getPath(SPACE.PRIVATE);
					DefaultNavigationProvider nv = new DefaultNavigationProvider(DemoVirtualHost.this);
					nv.setConnection(new AuthConnection( new FdConnection(CherryApi.DEFAULT_NAVIGATION_PROVIDER, new File(priv, "webcontent/nav"), false), new DefaultAuthorizator() ) );
					setNavigationProvider( nv );
					
					setRendererResolver(new DefaultRendererResolver());
					setResourceResolver(new DefaultResourceResolver());
					
					addResourceDataSource(new SharedDataSource(new AuthConnection( new FdConnection(CherryApi.DEFAULT_RESOURCE_PROVIDER, new File(priv, "webcontent/res"), false), new DefaultAuthorizator() ) ) );
					addResourceDataSource(new SharedDataSource(new AuthConnection( new FsConnection("pub", new File(priv, "webcontent/pub"), true, false), null ) ) );
					addResourceDataSource(new SharedDataSource(new AuthConnection( new FsConnection("aaa", new File(priv, "webcontent/aaa"), true, false), new ReadAllAuthorizator() ) ) );
					
					addApiProvider("base", new DefaultBaseApi());
					
					setAccountSource(new ResourceAccountSource( getResourceProvider("aaa") ));
					setAuthorizationSource(new ResourceAuthorizationSource( getResourceProvider("aaa") ));
					
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
		});

	}
	
}
