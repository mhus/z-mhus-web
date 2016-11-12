package de.mhus.cherry.portal.demo;

import java.io.File;

import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.impl.DefaultContentNodeResolver;
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
import de.mhus.lib.cao.auth.AuthCore;
import de.mhus.lib.cao.fdb.FdbCore;
import de.mhus.lib.cao.fs.FsCore;
import de.mhus.lib.cao.util.SharedDataSource;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.osgi.sop.api.Sop;

public class DemoVirtualHost extends DefaultVirtualHost {


	public DemoVirtualHost() {
		
		MThread.asynchron(new Runnable(){
			
			@Override
			public void run() {
				CherryApi api = Sop.waitForApi(CherryApi.class, 10000);
				try {
//					DeployDescriptor privDep = MThread.getWithTimeout(
//						() -> {
//							return api.getDeployDescritor(FrameworkUtil.getBundle(DemoVirtualHost.class));
//						}, MTimeInterval.MINUTE_IN_MILLISECOUNDS, false);

					DeployDescriptor privDep = api.getDeployDescritor(FrameworkUtil.getBundle(DemoVirtualHost.class));
					File priv = privDep.getPath(SPACE.PRIVATE);
					DefaultNavigationProvider nv = new DefaultNavigationProvider(DemoVirtualHost.this);
					nv.setConnection(new AuthCore( new FdbCore(CherryApi.DEFAULT_NAVIGATION_PROVIDER, new File(priv, "webcontent/nav"), false), new DefaultAuthorizator() ) );
					setNavigationProvider( nv );
					
					setRendererResolver(new DefaultRendererResolver());
					setResourceResolver(new DefaultResourceResolver());
					
					addResourceDataSource(new SharedDataSource(new AuthCore( new FdbCore(CherryApi.DEFAULT_RESOURCE_PROVIDER, new File(priv, "webcontent/res"), false), new DefaultAuthorizator() ) ) );
					addResourceDataSource(new SharedDataSource(new AuthCore( new FsCore("pub", new File(priv, "webcontent/pub"), true, false), null ) ) );
					addResourceDataSource(new SharedDataSource(new AuthCore( new FsCore("aaa", new File(priv, "webcontent/aaa"), true, false), new ReadAllAuthorizator() ) ) );
					
					addApiProvider("base", new DefaultBaseApi());
					
					setAccountSource(new ResourceAccountSource( getResourceProvider("aaa") ));
					setAuthorizationSource(new ResourceAuthorizationSource( getResourceProvider("aaa") ));
					
					setContentNodeResolver(new DefaultContentNodeResolver( DemoVirtualHost.this ));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
		});

	}
	
}
