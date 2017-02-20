package de.mhus.cherry.portal.impl;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.RendererResolver;
import de.mhus.cherry.portal.api.ResourceResolver;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.impl.aaa.DefaultAuthorizator;
import de.mhus.cherry.portal.impl.aaa.ReadAllAuthorizator;
import de.mhus.cherry.portal.impl.aaa.ResourceAccountSource;
import de.mhus.cherry.portal.impl.aaa.ResourceAuthorizationSource;
import de.mhus.cherry.portal.impl.api.DefaultBaseApi;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoDataSource;
import de.mhus.lib.cao.aspect.Changes;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.cao.auth.AuthCore;
import de.mhus.lib.cao.auth.AuthStructureControl;
import de.mhus.lib.cao.auth.Authorizator;
import de.mhus.lib.cao.fdb.FdbCore;
import de.mhus.lib.cao.fs.FsCore;
import de.mhus.lib.cao.util.DefaultChangesQueue;
import de.mhus.lib.cao.util.DefaultStructureControl;
import de.mhus.lib.cao.util.SharedDataSource;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.karaf.cao.CaoDataSourceUtil;
import de.mhus.osgi.sop.api.Sop;

/*

<bean id="vhost" class="de.mhus.cherry.portal.impl.CherryVirtualHost" init-method="doActivate" destroy-method="doDeactivate">
  <property name="bundleContext" ref="blueprintBundleContext"/>
  <property name="name" value="default"/>
</bean>

 */
public class CherryVirtualHost extends AbstractVirtualHost {

	
	private String navigationProviderName;
	private CaoDataSource navigationDataSource;
	private Authorizator navigationAuthorizator;
	private String defaultResourceProviderName;
	private CaoDataSource defaultResourceDataSource;
	private Authorizator defaultResourceAuthorizator;
	private String publicResourceProviderName;
	private CaoDataSource publicResourceDataSource;
	private String aaaResourceProviderName;
	private CaoDataSource aaaResourceDataSource;
	private boolean enableBaseApi = true;
	private CaoCore navCore;
	private CaoCore resCore;
	private CaoCore pubCore;
	private CaoCore aaaCore;
	private BundleContext bundleContext;
	
	@Override
	public void doActivate() {
		super.doActivate();
		try {
			
			// lookup navigation provider
			if (navigationDataSource == null)
				navigationDataSource = CaoDataSourceUtil.lookup(navigationProviderName);
			
			// prepare nav core
			navCore = navigationDataSource.getCore();
			if (navCore.getAspectFactory(StructureControl.class) == null)
				navCore.registerAspectFactory(StructureControl.class, new DefaultStructureControl(WidgetApi.SORT));
			if (navCore.getAspectFactory(Changes.class) == null)
				   navCore.registerAspectFactory(Changes.class, new DefaultChangesQueue());
	
			if (navigationAuthorizator == null)
				navigationAuthorizator = new DefaultAuthorizator();

			// set nav core
			DefaultNavigationProvider nv = new DefaultNavigationProvider(CherryVirtualHost.this);
			nv.setConnection(
					new AuthCore(CherryApi.DEFAULT_NAVIGATION_PROVIDER, navCore, navigationAuthorizator )
					.registerAspectFactory(StructureControl.class, new AuthStructureControl() )
				);
			setNavigationProvider( nv );
	
			// set default render resolver
			if (getRendererResolver() == null) 
				super.setRendererResolver(new DefaultRendererResolver());
			// set default resource resolver
			if (getResourceResolver() == null)
				super.setResourceResolver(new DefaultResourceResolver());
			// set default content node resolver
			if (getContentNodeResolver() == null)
				setContentNodeResolver(new DefaultContentNodeResolver(this));
			
			
			// lookup default resource provider
			if (defaultResourceDataSource == null)
				defaultResourceDataSource = CaoDataSourceUtil.lookup(defaultResourceProviderName);

			resCore = defaultResourceDataSource.getCore();
			if (resCore.getAspectFactory(StructureControl.class) == null)
				resCore.registerAspectFactory(StructureControl.class, new DefaultStructureControl(WidgetApi.SORT));
			if (resCore.getAspectFactory(Changes.class) == null)
				resCore.registerAspectFactory(Changes.class, new DefaultChangesQueue());

			if (defaultResourceAuthorizator == null)
				defaultResourceAuthorizator = new DefaultAuthorizator();

			// set
			addResourceDataSource(
					new SharedDataSource(
							new AuthCore(CherryApi.DEFAULT_RESOURCE_PROVIDER, resCore, defaultResourceAuthorizator ) 
						.registerAspectFactory(StructureControl.class, new AuthStructureControl() )
					) 
				);
			
			// lookup and set public content
			if (publicResourceDataSource == null && publicResourceProviderName != null)
				publicResourceDataSource = CaoDataSourceUtil.lookup(publicResourceProviderName);
			if (publicResourceDataSource != null) {
				// prepare
				pubCore = publicResourceDataSource.getCore();
				if (pubCore.getAspectFactory(StructureControl.class) == null)
					pubCore.registerAspectFactory(StructureControl.class, new DefaultStructureControl(WidgetApi.SORT));
				// set
				addResourceDataSource(
						new SharedDataSource(
							new AuthCore( CherryApi.PUBLIC_RESOURCE_PROVIDER, pubCore, null )
								.registerAspectFactory(StructureControl.class, new AuthStructureControl() )
							) 
						);
			}	
			
			// lookup and set aaa resource
			if (aaaResourceDataSource == null)
				aaaResourceDataSource = CaoDataSourceUtil.lookup(aaaResourceProviderName);

			aaaCore = aaaResourceDataSource.getCore();
			if (aaaCore.getAspectFactory(StructureControl.class) == null)
				aaaCore.registerAspectFactory(StructureControl.class, new DefaultStructureControl(WidgetApi.SORT));
			if (aaaCore.getAspectFactory(Changes.class) == null)
				aaaCore.registerAspectFactory(Changes.class, new DefaultChangesQueue());

			addResourceDataSource(
					new SharedDataSource(
						new AuthCore( CherryApi.AAA_RESOURCE_PROVIDER, aaaCore, new ReadAllAuthorizator() ) 
							.registerAspectFactory(StructureControl.class, new AuthStructureControl() )
						) 
					);

			if (enableBaseApi ) {
				addApiProvider("base", new DefaultBaseApi());
			}
			
			setAccountSource(new ResourceAccountSource( getResourceProvider(CherryApi.AAA_RESOURCE_PROVIDER) ));
			setAuthorizationSource(new ResourceAuthorizationSource( getResourceProvider(CherryApi.AAA_RESOURCE_PROVIDER) ));

			
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	@Override
	public void doDeactivate() {
		// close all connections
		if (navCore != null) navCore.close();
		navCore = null;
		if (resCore != null) resCore.close();
		resCore = null;
		if (pubCore != null) pubCore.close();
		pubCore = null;
		if (aaaCore != null) aaaCore.close();
		aaaCore = null;
		super.doDeactivate();
	}


	public Authorizator getNavigationAuthorizator() {
		return navigationAuthorizator;
	}


	public void setNavigationAuthorizator(Authorizator authorizator) {
		this.navigationAuthorizator = authorizator;
	}


	public CaoDataSource getNavigationDataSource() {
		return navigationDataSource;
	}


	public void setNavigationDataSource(CaoDataSource navigationDataSource) {
		this.navigationDataSource = navigationDataSource;
	}

	public String getDefaultResourceProviderName() {
		return defaultResourceProviderName;
	}


	public void setDefaultResourceProviderName(String defaultResourceProviderName) {
		this.defaultResourceProviderName = defaultResourceProviderName;
	}


	public CaoDataSource getDefaultResourceDataSource() {
		return defaultResourceDataSource;
	}


	public void setDefaultResourceDataSource(CaoDataSource defaultResourceDataSource) {
		this.defaultResourceDataSource = defaultResourceDataSource;
	}


	public Authorizator getDefaultResourceAuthorizator() {
		return defaultResourceAuthorizator;
	}


	public void setDefaultResourceAuthorizator(Authorizator defaultResourceAuthorizator) {
		this.defaultResourceAuthorizator = defaultResourceAuthorizator;
	}


	public String getPublicResourceProviderName() {
		return publicResourceProviderName;
	}


	public void setPublicResourceProviderName(String publicResourceProviderName) {
		this.publicResourceProviderName = publicResourceProviderName;
	}


	public CaoDataSource getPublicResourceDataSource() {
		return publicResourceDataSource;
	}


	public void setPublicResourceDataSource(CaoDataSource publicResourceDataSource) {
		this.publicResourceDataSource = publicResourceDataSource;
	}


	public String getAaaResourceProviderName() {
		return aaaResourceProviderName;
	}


	public void setAaaResourceProviderName(String aaaResourceProviderName) {
		this.aaaResourceProviderName = aaaResourceProviderName;
	}


	public CaoDataSource getAaaResourceDataSource() {
		return aaaResourceDataSource;
	}


	public void setAaaResourceDataSource(CaoDataSource aaaResourceDataSource) {
		this.aaaResourceDataSource = aaaResourceDataSource;
	}


	public boolean isEnableBaseApi() {
		return enableBaseApi;
	}


	public void setEnableBaseApi(boolean enableBaseApi) {
		this.enableBaseApi = enableBaseApi;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
}
