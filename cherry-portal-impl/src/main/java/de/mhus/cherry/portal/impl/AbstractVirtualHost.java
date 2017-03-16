package de.mhus.cherry.portal.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import com.vaadin.client.WidgetUtil;

import de.mhus.cherry.portal.api.ActionCallback;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ContentNodeResolver;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.LoginHandler;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.RendererResolver;
import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ResourceResolver;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.StructureChangesListener;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.WidgetDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.EditorFactory.TYPE;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoDataSource;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;
import de.mhus.lib.core.AbstractProperties;
import de.mhus.lib.core.MEventHandler;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.base.service.TimerIfc;
import de.mhus.lib.core.security.AccountSource;
import de.mhus.lib.core.security.AuthorizationSource;
import de.mhus.lib.core.util.FileResolver;
import de.mhus.lib.core.util.ReadOnlyList;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.BundleLocal;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.lib.servlet.ResponseWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.action.ActionApi;
import de.mhus.osgi.sop.api.action.ActionDescriptor;

public class AbstractVirtualHost extends MLog implements VirtualHost, Named {

	private static final String SESSION_RESOURCE_PROVIDER = "_cherry_resource_provider_";
	
	private NavigationProvider navigationProvider;
	private ResourceResolver resourceResolver;
	private RendererResolver rendererResolver;
	private HashMap<String, CaoDataSource> resourceProvider = new HashMap<>();
	private LinkedList<LoginHandler> loginHandlers = new LinkedList<>();
	private HashMap<String, ResourceRenderer> apiProvider = new HashMap<>();
	private HashMap<String, List<String>> configurationLists = new HashMap<>();
	private HashMap<String, ResourceProvider> hostResourceProviders = new HashMap<>();
	private ContentNodeResolver contentNodeResolver;
	private MEventHandler<StructureChangesListener> structureHandler = new MEventHandler<>();
	
	private AccountSource accountSource;

	private AuthorizationSource authorizationSource;

	private String name;

	private TimerIfc timer = MApi.lookup(TimerIfc.class);

	private String fileOverlayPath;

	private BundleLocal<FileResolver> privateFileResolver;

	private EditorFactory defaultNavigationEditorFactory;
	
	protected HashMap<String, WidgetDescriptor> widgetDescriptors = new HashMap<>();
	

	public AbstractVirtualHost() {
	}

	public void doActivate() {
//		TimerFactory timerFactory = MOsgi.getService(TimerFactory.class);
//		setTimerFactory(timerFactory);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				doUpdates();
			}
		}, 5000, 1000);

		privateFileResolver = new BundleLocal<>();
		privateFileResolver.open();
	}
	
	public void doDeactivate() {
		timer.cancel();
		if (privateFileResolver != null)
			privateFileResolver.close();
	}
	
	@Override
	public void sendError(CallContext call, int sc) {
		try {
			if (!call.getHttpResponse().isCommitted())
				call.getHttpResponse().sendError(sc);
			else {
				//TODO send error in content ...
				call.getHttpResponse().getOutputStream().print("An Error Occured !" );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	@Override
	public synchronized void doUpdates() {
		if (navigationProvider != null) {
			Change[] changes = navigationProvider.getChanges();
						
			if (changes != null && changes.length != 0) {
				log().d("fire navigation changes",changes.length);
				try {
					structureHandler.fireMethod(StructureChangesListener.class.getMethod("navigationChanges", Change[].class), new Object[] {changes} );
				} catch(NoSuchMethodException e) {
					log().e(e);
				}
			}
		}
	}

	@Override
	public CaoNode getNavigationResource(String path) {
		
		NavigationProvider navProvider = getNavigationProvider();
		if (navProvider == null)
			return null;
		
		NavNode navResource = navProvider.getNode(path);
		if (navResource == null)
			return null;
		
		String subPath = "";
		String control = "";
		String subType = ""; // type of path after nav node, nav/.content/res/res/
		boolean isFolder = false;
		if (MString.isIndex(path, '.')) {
			control = MString.afterIndex(path, '.');
			path = MString.beforeIndex(path, '.');
		}
		
		if (path.endsWith("/")) {
			isFolder = true;
			path = path.substring(0, path.length()-1);
		}
		
		if (MString.isIndex(control, '/')) {
			subPath = MString.afterIndex(control, '/');
			control = MString.beforeIndex(control, '/');
		}
		
		// if folder, the first part is the type of sub resource
		if (isFolder) {
			if (MString.isIndex(control, '.')) {
				subType = MString.beforeIndex(control, '.');
				control = MString.afterIndex(control, '.');
			} else {
				subType = control;
				control = "";
			}
		}
		// last one is return type
		if (MString.isIndex(control, '.')) {
			control = MString.beforeLastIndex(control, '.');
		} else {
			control = "";
		}
		
		CaoNode resResource = navResource.getRes();
		if (resResource == null)
			return null;

		if (isFolder && MString.isSet(subType)) {
			
			if ("content".equals(subType)) {
				
				for (String part : subPath.split("/")) {
					if (MString.isSetTrim(part)) {
						resResource = (CaoNode) resResource.getNode(part);
						if (resResource == null)
							return null;
					}
				}
				
			}
			
		}

		return resResource;
	}

	@Override
	public void processRequest(CallContext iCall) {
		CherryCallContext call = (CherryCallContext)iCall;
		
		NavigationProvider navProvider = getNavigationProvider();
		
		try {
			if (navProvider == null) {
				call.getHttpResponse().sendError(HttpServletResponse.SC_BAD_GATEWAY);
				return;
			}
			
			
			String path = call.getHttpPath();
			
			
			NavNode navResource = navProvider.getNode(path);
			
			if (navResource == null) {
				sendError(call, HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			call.setNavigationResource(navResource);

			String subPath = "";
			String control = "";
			String subType = ""; // type of path after nav node, nav/.content/res/res/
			MProperties selectors = null;
			String retType = "";
			boolean isFolder = false;
			if (MString.isIndex(path, '.')) {
				control = MString.afterIndex(path, '.');
				path = MString.beforeIndex(path, '.');
			}
			
			if (path.endsWith("/")) {
				isFolder = true;
				path = path.substring(0, path.length()-1);
			}
			
			if (MString.isIndex(control, '/')) {
				subPath = MString.afterIndex(control, '/');
				control = MString.beforeIndex(control, '/');
			}
			
			// if folder, the first part is the type of sub resource
			if (isFolder) {
				if (MString.isIndex(control, '.')) {
					subType = MString.beforeIndex(control, '.');
					control = MString.afterIndex(control, '.');
				} else {
					subType = control;
					control = "";
				}
			}
			// last one is return type
			if (MString.isIndex(control, '.')) {
				retType = MString.afterLastIndex(control, '.').toLowerCase();
				control = MString.beforeLastIndex(control, '.');
			} else {
				retType = control;
				control = "";
			}
			// the rest are selectors
			if (MString.isSet(control))
				selectors = MProperties.explodeToMProperties(control.split("\\."), ':');
			
			CaoNode resResource = navResource.getRes();
			if (resResource == null) {
				log().d("content not found", call);
				sendError(call, HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			call.setMainResource(resResource);
			call.setSelectors(selectors);
			
			String resName = resResource.getName();
			if (MString.isEmpty(retType))
				retType = resResource.getString(CherryApi.RES_RET_TYPE, null);
			if (MString.isEmpty(retType) && resName != null && MString.isIndex(resName, '.') )
				retType = MString.afterLastIndex(resName, '.');
			call.setReturnType(retType);
			
			if (isFolder && MString.isSet(subType)) {
				
				if (CherryApi.REF_CONTENT.equals(subType)) {
					
					for (String part : subPath.split("/")) {
						if (MString.isSetTrim(part)) {
							resResource = (CaoNode) resResource.getNode(part);
							if (resResource == null) {
								log().d("sub resource not found", call, subPath);
								sendError(call, HttpServletResponse.SC_NOT_FOUND);
								return;
							}
						}
					}
					
				} else
				if (CherryApi.REF_DATA.equals(subType)) {
					String[] subParts = subPath.split("/");
					CaoNode subNode = null;
					if (subParts.length > 0) {
						String linkName = subParts[0];
						String dataName = resResource.getString( CherryApi.DATA_PREFIX + linkName, null);
						if (MString.isSet(dataName)) {
							String dataProviderName = MString.beforeIndex(dataName, ':');
							String dataPath = MString.afterIndex(dataName, ':');
							ResourceProvider subProvider = getResourceProvider(dataProviderName);
							if (subProvider != null) {
								subNode = subProvider.getResource(dataPath);
								if (subNode != null) {
									subParts[0] = "";
									for (String part : subParts) {
										if (MString.isSetTrim(part)) {
											subNode = subNode.getNode(part);
											if (subNode == null) break;
										}
									}
								}
							}
							
						} else {
							log().d("datasource not found", linkName, path);
						}
						
					}
					
					if (subNode != null) {
						resResource = subNode;
					} else {
						sendError(call, HttpServletResponse.SC_NOT_FOUND);
						return;
					}
					
				} else
				if (CherryApi.REF_RES.equals(subType)) {
					// nothing todo ...
				} else {
					log().d("unknown subType", call, subType);
					sendError(call, HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
			}
			call.setResource(resResource);

			getRendererResolver().getRenderer(iCall).doRender(iCall);
			
			
		} catch (NotFoundException t) {
			log().d("not found",call,t);
			sendError(call, HttpServletResponse.SC_NOT_FOUND);
		} catch (Throwable t) {
			UUID id = UUID.randomUUID();
			log().w("internal error", id,call,t);
			sendError(call, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public NavigationProvider getNavigationProvider() {
		return navigationProvider;
	}

	public void setNavigationProvider(NavigationProvider navigationProvider) {
		this.navigationProvider = navigationProvider;
	}

	@Override
	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	@Override
	public ResourceProvider getResourceProvider(String name) {
		CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
		name = name.toLowerCase();
		
		ResourceProvider provider = null;
		synchronized (this) {
			if (call == null) {
				provider = hostResourceProviders.get(name);
				if (provider == null) {
					try {
						provider = new DefaultResourceProvider( resourceProvider.get(name).getConnection() );
						hostResourceProviders.put(name, provider);
					} catch (Exception e) {
						log().w(e);
					}
				}
			} else {
				provider = (ResourceProvider) call.getSession().get(SESSION_RESOURCE_PROVIDER + name );
				if (provider == null) {
					try {
						provider = new DefaultResourceProvider( resourceProvider.get(name).getConnection() );
						call.getSession().put(SESSION_RESOURCE_PROVIDER + name, provider );
					} catch (Exception e) {
						log().w(e);
					}
				}
			}
		}
//		if (provider == null)
//			try {
//				provider = MOsgi.getService(ResourceProvider.class, MOsgi.filterServiceName("cherry_resource_" + name));
//			} catch (NotFoundException e) {}
		return provider;
	}

	public void setResourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	@Override
	public RendererResolver getRendererResolver() {
		return rendererResolver;
	}

	public void setRendererResolver(RendererResolver rendererResolver) {
		this.rendererResolver = rendererResolver;
	}

	@Override
	public ResourceRenderer getResourceRenderer(String name) {
		name = name.toLowerCase();
		ResourceRenderer renderer = null;
		try {
			renderer = MOsgi.getService(ResourceRenderer.class, MOsgi.filterServiceName("cherry_renderer_" + name));
		} catch (NotFoundException e) {}
		return renderer;
	}

	public void addResourceDataSource(CaoDataSource provider) {
		resourceProvider.put(provider.getName(), provider);
	}

	public void addResourceDataSource(String name, CaoDataSource provider) {
		resourceProvider.put(name, provider);
	}
	
	@Override
	public String getDefaultContentType() {
		return "text/html";
	}

//	@Override
//	public EditorFactory getControlEditorFactory(String name) {
//		descriptor = getWidgetDescriptor(name);
//		name = name.toLowerCase();
//		EditorFactory factory = null;
//		try {
//			factory = MOsgi.getService(EditorFactory.class, MOsgi.filterServiceName("cherry_editor_" + name));
//		} catch (NotFoundException e) {}
//		return factory;
//	}

	@Override
	public WidgetDescriptor getWidgetDescriptor(String name) {
		name = name.toLowerCase();
		WidgetDescriptor ret = widgetDescriptors.get(name);
		if (ret == null) log().w("widget descriptor not found",name);
		return ret;
	}
	
	public void addWidgetDescriptor(WidgetDescriptor wd) {
		widgetDescriptors.put(wd.getName().toLowerCase(), wd);
	}

	@Override
	public ScriptRenderer getScriptRenderer(String name) {
		name = name.toLowerCase();
		ScriptRenderer renderer = null;
		try {
			renderer = MOsgi.getService(ScriptRenderer.class, MOsgi.filterServiceName("cherry_script_renderer_" + name));
		} catch (NotFoundException e) {}
		return renderer;
	}

	@Override
	public AaaContext doLogin(RequestWrapper request, ResponseWrapper response) {
		AaaContext out = null;
		for (LoginHandler handler : loginHandlers) {
			out = handler.doLogin(request, response);
			if (out != null) {
				log().d("login handled", handler.getClass(), handler);
				return out;
			}
		}
		return null;
	}
	
	public void addLoginHandler(LoginHandler handler) {
		loginHandlers.add(handler);
	}
	
	public void addApiProvider(String name, ResourceRenderer renderer) {
		apiProvider.put(name, renderer);
	}

	@Override
	public void processApiRequest(CallContext call) {
		
		call.resetPath();
		String path = call.consumePath();
		if (path == null) {
			sendError(call, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		ResourceRenderer provider = apiProvider.get(path);
		if (provider == null) {
			sendError(call, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		try {
			provider.doRender(call);
		} catch (Throwable e) {
			log().d(path,e);
			sendError(call, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
	}

	@Override
	public List<String> getConfigurationList(String name) {
		return configurationLists.get(name);
	}
	
	@Override
	public void setConfigurationList(String name, List<String> list) {
		configurationLists.put(name, new ReadOnlyList<String>(list));
	}

	@Override
	public AccountSource getAccountSource() {
		return accountSource;
	}
	
	public void setAccountSource(AccountSource accountSource) {
		this.accountSource = accountSource;
	}

	@Override
	public AuthorizationSource getAuthorizationSource() {
		return authorizationSource;
	}
	
	public void setAuthorizationSource(AuthorizationSource authorizationSource) {
		this.authorizationSource = authorizationSource;
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, name);
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ContentNodeResolver getContentNodeResolver() {
		return contentNodeResolver;
	}

	public void setContentNodeResolver(ContentNodeResolver contentNodeResolver) {
		this.contentNodeResolver = contentNodeResolver;
	}

	@Override
	public ActionCallback getActionCallback(String actionName) {
		actionName = actionName.toLowerCase();
		ActionCallback factory = null;
		try {
			factory = MOsgi.getService(ActionCallback.class, MOsgi.filterServiceName("cherry_callback_" + actionName));
		} catch (NotFoundException e) {}
		return factory;
	}

	@Override
	public Set<String> getConfigurationListName() {
		return configurationLists.keySet();
	}

	@Override
	public Collection<EditorFactory> getAvailablePageTypes(CaoNode nav) {
		return CherryUtil.orderServices(VirtualHost.class, EditorFactory.class, this)
				.stream()
				.filter(i -> i.getType() == TYPE.PAGE )
				.collect(Collectors.toList());
	}

	@Override
	public Collection<EditorFactory> getAvailableWidgetTypes(CaoNode nav) {
		return CherryUtil.orderServices(VirtualHost.class, EditorFactory.class, this)
				.stream()
				.filter(i -> i.getType() == TYPE.WIDGET )
				.collect(Collectors.toList());
	}
	
	@Override
	public Collection<ActionDescriptor> getActions(String type, CaoNode[] node) {
		LinkedList<String> tags = new LinkedList<>();
		tags.add( "control" );
		tags.add( "caonode" );
		tags.add( type );
		
		MProperties properties = new MProperties();
		properties.put(CherryUtil.NODE, node);
		List<ActionDescriptor> actions = Sop.getApi(ActionApi.class).getActions(tags, properties);
		
		actions = CherryUtil.order("control_action_" + type, actions, this);
		return actions;
	}

	@Override
	public MEventHandler<StructureChangesListener> getStructureRegistry() {
		return structureHandler;
	}

	@Override
	public void doPrepareCreatedWidget(CaoNode res) {
		EditorFactory factory = Sop.getApi(WidgetApi.class).getControlEditorFactory(this, res);
		if (factory != null)
			factory.doPrepareCreatedWidget(res);
	}

	@Override
	public synchronized FileResolver getPrivateFileResolver(Bundle bundle) {
		FileResolver out = privateFileResolver.get(bundle);
		if (out == null) {
			out = createPrivateFileResolver(bundle);
			privateFileResolver.put(bundle, out);
		}
		return out;
	}

	protected FileResolver createPrivateFileResolver(Bundle bundle) {
		return new PrivateFileResolver(bundle);
	}
	
	public String getFileOverlayPath() {
		return fileOverlayPath;
	}

	public void setFileOverlayPath(String fileOverlayPath) {
		this.fileOverlayPath = fileOverlayPath;
	}

	private class PrivateFileResolver implements FileResolver {

		
		private Bundle bundle;
		private File root;

		public PrivateFileResolver(Bundle bundle) {
			this.bundle = bundle;
			DeployDescriptor descriptor = Sop.getApi(CherryApi.class).getDeployDescritor(bundle);
			if (descriptor != null)
				root = descriptor.getPath(SPACE.PRIVATE);
		}

		@Override
		public File getFile(String path) {
			// check for overlay file
			if (fileOverlayPath != null) {
				String fp = fileOverlayPath + "/" + bundle.getSymbolicName() + "/private/" + MFile.normalizePath(path);
				File f = new File(fp);
				log().d("Overlay", f.exists(), fp);
				if (f.exists() && f.isFile()) {
					return f;
				}
			}
			
			if (root == null) return null;
			File file = new File(root, path);
			return file;
		}

		@Override
		public Set<String> getContent(String path) {
			return null;
		}

		@Override
		public File getRoot() {
			if (root == null) return null;
			return root;
		}

	}

	@Override
	public ResourceRenderer lookupTheme(NavNode navigation) {
		String themeName = getContentNodeResolver().getRecursiveString(navigation, WidgetApi.THEME );
		if (MString.isSet(themeName)) {
			return getResourceRenderer(themeName);
		}
		return null;
	}

	@Override
	public EditorFactory getDefaultEditorFactory(CaoNode resource) {
		if (CherryUtil.isNavigationNode(this, resource))
			return defaultNavigationEditorFactory;
		return null;
	}

	public EditorFactory getDefaultNavigationEditorFactory() {
		return defaultNavigationEditorFactory;
	}

	public void setDefaultNavigationEditorFactory(EditorFactory defaultNavigationEditorFactory) {
		this.defaultNavigationEditorFactory = defaultNavigationEditorFactory;
	}

}
