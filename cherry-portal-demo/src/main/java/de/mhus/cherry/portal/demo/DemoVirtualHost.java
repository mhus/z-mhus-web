package de.mhus.cherry.portal.demo;

import java.io.File;

import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.WidgetDescriptor;
import de.mhus.cherry.portal.impl.CherryVirtualHost;
import de.mhus.lib.cao.fdb.FdbCore;
import de.mhus.lib.cao.fs.FsCore;
import de.mhus.lib.cao.util.SharedDataSource;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.osgi.sop.api.Sop;

public class DemoVirtualHost extends CherryVirtualHost {


	@Override
	public void doActivate() {
		
		// need to run asynchrony to wait for the 'content deployer' to deploy the resources before
		// connect the FdbCore - this is a demo environment, should be defined as registered datasource
		// in productive systems.
		
		MThread.asynchron(new Runnable(){
			
			@Override
			public void run() {
				CherryApi api = Sop.waitForApi(CherryApi.class, 10000);
				try {
					// get my deploy descriptor to set data sources into bundle deployed resources
					DeployDescriptor privDep = MThread.getWithTimeout(
						() -> {
							return api.getDeployDescritor(FrameworkUtil.getBundle(DemoVirtualHost.class));
						}, MTimeInterval.MINUTE_IN_MILLISECOUNDS, false);
					
					// private part of the deployed resources
					File priv = privDep.getPath(SPACE.PRIVATE);

					// set the data sources
					setNavigationDataSource(new SharedDataSource(
							new FdbCore(CherryApi.DEFAULT_NAVIGATION_PROVIDER, new File(priv, "webcontent/nav"), false), false));
					setDefaultResourceDataSource(new SharedDataSource(
							new FdbCore(CherryApi.DEFAULT_RESOURCE_PROVIDER, new File(priv, "webcontent/res"), false), false));
					setPublicResourceDataSource(new SharedDataSource(
							new FsCore(CherryApi.PUBLIC_RESOURCE_PROVIDER, new File(priv, "webcontent/pub"), true, false), false));
					setAaaResourceDataSource(new SharedDataSource(
							new FsCore(CherryApi.AAA_RESOURCE_PROVIDER, new File(priv, "webcontent/aaa"), true, false), false));
				} catch (Throwable t) {
					log().e(t);
				}
				
				addWidgetDescriptor(new SimpleWidgetDescriptor("de.mhus.cherry.portal.impl.page.SimpleWidget", WidgetDescriptor.TYPE.WIDGET, new SimpleWidget(), new SimpleEditorFactory() ) );
				addWidgetDescriptor(new SimpleWidgetDescriptor("de.mhus.cherry.portal.impl.page.SimplePage", WidgetDescriptor.TYPE.PAGE, new SimplePage(), new SimpleEditorFactory() ) );
				addWidgetDescriptor(new SimpleWidgetDescriptor("de.mhus.cherry.portal.impl.page.SimpleTheme", WidgetDescriptor.TYPE.THEME, new SimpleTheme(), null ) );
				
				String overlayPath = "demo/overlay";
				setFileOverlayPath(overlayPath);
				setDefaultNavigationEditorFactory(new NavigationEditor());
				DemoVirtualHost.super.doActivate();
			}
		});

	}
	
}
