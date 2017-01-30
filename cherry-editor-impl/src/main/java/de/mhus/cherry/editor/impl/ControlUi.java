package de.mhus.cherry.editor.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.util.CurrentInstance;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.control.GuiApi;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiSpaceService;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.cfg.CfgString;
import de.mhus.lib.core.lang.ValueProvider;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.security.AccessControl;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.errors.MException;
import de.mhus.lib.vaadin.VaadinAccessControl;
import de.mhus.lib.vaadin.servlet.VaadinRequestWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AccessApi;

@Theme("cherrytheme")
@Widgetset("de.mhus.cherry.editor.theme.CherryWidgetset")
//@JavaScript({"https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"})
@JavaScript({"../../../.pub/base/sys/jquery-3.1.1.min.js"})
//@Push
public class ControlUi extends UI implements GuiApi {

	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLog(ControlUi.class);
	private MenuBar menuBar;
	private AccessControl accessControl;
	private Desktop desktop;
	private ServiceTracker<GuiSpaceService,GuiSpaceService> spaceTracker;
	private TreeMap<String,GuiSpaceService> spaceList = new TreeMap<String, GuiSpaceService>();
	private HashMap<String, AbstractComponent> spaceInstanceList = new HashMap<String, AbstractComponent>(); 
	private BundleContext context;
	private String trailConfig = null;
	private String host;
	private LinkedList<String> history = new LinkedList<>();

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout content = new VerticalLayout();
		setContent(content);
		content.setSizeFull();
        content.addStyleName("view-content");
        content.setMargin(true);
        content.setSpacing(true);

        context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		spaceTracker = new ServiceTracker<>(context, GuiSpaceService.class, new GuiSpaceServiceTrackerCustomizer() );
		spaceTracker.open();

        host = request.getHeader("Host");
        
        // get User
        accessControl = new UiAccessControl(request);
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginScreen.LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
		

	}

	private void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        desktop = new Desktop(this);
        setContent(desktop);
		synchronized (this) {
			desktop.refreshSpaceList(spaceList);
		}
				
		String nav = UI.getCurrent().getPage().getUriFragment();
		
		if (nav != null) {
			
			if (nav.startsWith("!")) nav=nav.substring(1);
			
			if (MString.isIndex(nav, ':')) {
				String backLink = MString.beforeIndex(nav, ':');
				nav = MString.afterIndex(nav, ':');
				if (MString.isSet(backLink))
					rememberNavigation("Webseite", "site", "", backLink, false );
			}
			
			String[] parts = nav.split("/", 3);
			if (parts.length > 0) {
				String space = parts[0];
				String subSpace = parts.length > 1 ? parts[1] : null;
				String filter = parts.length > 2 ? parts[2] : null;
				
				openSpace(space, subSpace, filter, true, false);

			}
			
		}
		
		
	}

	public void rememberNavigation(String caption, String space, String subSpace, String search, boolean navLink) {
		String newEntry = caption.replace('|', ' ') + "|" + space + "|" + subSpace + "|" + search;
		while (this.history.remove(newEntry) ) {} // move up
		this.history.add(newEntry);
		doUpdateHistoryMenu();
		if (navLink)
			UI.getCurrent().getPage().setUriFragment("!:" + space + "/" + (subSpace == null ? "" : subSpace) + "/" + (search == null ? "" : search));
	}

	@Override
	public void close() {
		synchronized (this) {
			spaceTracker.close();
			spaceList.clear();
			for (AbstractComponent v : spaceInstanceList.values())
				if (v instanceof GuiLifecycle) ((GuiLifecycle)v).doDestroy();
			spaceInstanceList.clear();
		}
		super.close();
	}

	private class GuiSpaceServiceTrackerCustomizer implements ServiceTrackerCustomizer<GuiSpaceService,GuiSpaceService> {

		@Override
		public GuiSpaceService addingService(
				ServiceReference<GuiSpaceService> reference) {
			synchronized (this) {
				GuiSpaceService service = context.getService(reference);
				spaceList.put(service.getName(),service);
				if (desktop != null) desktop.refreshSpaceList(spaceList);
				return service;
			}
		}

		@Override
		public void modifiedService(
				ServiceReference<GuiSpaceService> reference,
				GuiSpaceService service) {
			synchronized (this) {
				spaceList.remove(service.getName());
				AbstractComponent v = spaceInstanceList.remove(service.getName());
				if (v instanceof GuiLifecycle) ((GuiLifecycle)v).doDestroy();
				service = context.getService(reference);
				spaceList.put(service.getName(),service);
				if (desktop != null) desktop.refreshSpaceList(spaceList);
			}
		}

		@Override
		public void removedService(ServiceReference<GuiSpaceService> reference,
				GuiSpaceService service) {
			synchronized (this) {
				spaceList.remove(service.getName());
				AbstractComponent v = spaceInstanceList.remove(service.getName());
				if (v instanceof GuiLifecycle) ((GuiLifecycle)v).doDestroy();
				if (desktop != null) desktop.refreshSpaceList(spaceList);
			}
		}
	}

	public AbstractComponent getSpaceComponent(String name) {
		GuiSpaceService space = spaceList.get(name);
		if (space == null) return null;
		AbstractComponent instance = spaceInstanceList.get(name);
		if (instance == null) {
			instance = space.createSpace();
			if (instance == null) return null;
			if (instance instanceof GuiLifecycle) ((GuiLifecycle)instance).doInitialize();
			spaceInstanceList.put(name, instance);
		}
		return instance;
	}

	public BundleContext getContext() {
		return context;
	}
	
	public GuiSpaceService getSpace(String name) {
		return spaceList.get(name);
	}
	
	public AccessControl getAccessControl() {
		return accessControl;
	}

	public void removeSpaceComponent(String name) {
		AbstractComponent c = spaceInstanceList.remove(name);
		if (c != null && c instanceof GuiLifecycle) ((GuiLifecycle)c).doDestroy();
	}

	@Override
	public boolean hasAccess(String role) {
		if (role == null || accessControl == null || !accessControl.isUserSignedIn())
			return false;

		return Sop.getApi(AccessApi.class).hasGroupAccess(accessControl.getAccount(), getClass(), role, null);
		
	}
	
	@Override
	public boolean openSpace(String spaceId, String subSpace, String search) {
		return openSpace(spaceId, subSpace, search, true, true);
	}
	
	public boolean openSpace(String spaceId, String subSpace, String search, boolean history, boolean navLink) {
		GuiSpaceService space = getSpace(spaceId);
		if (space == null) return false;
		if (!hasAccess(space.getName()) || !space.hasAccess(getAccessControl())) return false;

		String ret = desktop.showSpace(space, subSpace, search);
		if (ret != null && history) {
			String newEntry = ret.replace('|', ' ') + "|" + spaceId + "|" + subSpace + "|" + search;
			while (this.history.remove(newEntry) ) {} // move up
			this.history.add(newEntry);
			doUpdateHistoryMenu();
		}
		if (navLink)
			UI.getCurrent().getPage().setUriFragment("!:" + spaceId + "/" + (subSpace == null ? "" : subSpace) + "/" + (search == null ? "" : search));
		return ret != null;
	}

	
	private void doUpdateHistoryMenu() {
		desktop.doUpdateHistoryMenu(history);
	}

	@Override
	public Subject getCurrentUser() {
		return (Subject)getSession().getAttribute(VaadinAccessControl.SUBJECT_ATTR);
	}

	public void requestBegin(HttpServletRequest request) {
//		this.httpRequest = request;
		if (trailConfig != null)
			MLogUtil.setTrailConfig(trailConfig);
		else
			MLogUtil.releaseTrailConfig();
		
		// touch session to avoid timeout
		
	}

	public void requestEnd() {
		MLogUtil.releaseTrailConfig();
	}

	public String getTrailConfig() {
		return trailConfig;
	}

	public void setTrailConfig(String trailConfig) {
		if (trailConfig == null) {
			this.trailConfig = trailConfig;
			MLogUtil.releaseTrailConfig();
		} else {
			MLogUtil.setTrailConfig(trailConfig);
			this.trailConfig = MLogUtil.getTrailConfig();
		}
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void navigateToEditor(CaoNode content) {
		openSpace("editor", null, content.getConnection().getName() + ":" + content.getId() );
	}

	@Override
	public void navigateBack() {
		if (history.size() == 0) return;
		String link = history.removeLast();
		if (history.size() == 0) return;
		link = history.getLast();
		doUpdateHistoryMenu();
		String[] parts = link.split("\\|", 4);
		if (parts[2].equals("null")) parts[2] = null;
		if (parts[3].equals("null")) parts[3] = null;
		openSpace(parts[1], parts[2], parts[3], false, true);
	}
	
//    @Override
//	public VaadinSession getSession() {
//        VaadinSession s = super.getSession();
//        CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
//        if (call == null) {
//        	call = (CallContext) s.getAttribute("__vc");
//        	if (call != null) {
//        		Sop.getApi(InternalCherryApi.class).setCallContext(call);
//        	}
//        }
//        return s;
//    }
    
/*
    @Override
    public VaadinSession getSession() {
    	// fix a problem with PUSH servlet ... the push will not call the default vaadin servlet and call is not initialized
        VaadinSession s = super.getSession();
//        log.i("GET SESSION",s);
//        VaadinRequest request = CurrentInstance.get(VaadinRequest.class);
//        VaadinResponse response = CurrentInstance.get(VaadinResponse.class);
        VaadinServletRequest cur = (VaadinServletRequest) VaadinService.getCurrentRequest();
        HttpServletRequest hreq = null;
        CallContext call = Sop.getApi(CherryApi.class).getCurrentCall();
        if (cur != null) {
	        hreq = (cur).getHttpServletRequest();
	//        log.i("CURRENT Request", hreq, call);
	//				call = Sop.getApi(InternalCherryApi.class).createCall(ControlServlet.getCurrent(), hreq, null);
        }
        if (call == null) {
        	call = CherryUtil.prepareHttpRequest(ControlServlet.getCurrent(), hreq, null);
        	if (call != null) log.i("Create call on the fly");
        }
        return s;
    }
*/
	
	/*
java.lang.NullPointerException
	at de.mhus.cherry.portal.impl.InternalCherryApiImpl.createCall(InternalCherryApiImpl.java:91)
	at de.mhus.cherry.portal.api.util.CherryUtil.prepareHttpRequest(CherryUtil.java:117)
	at de.mhus.cherry.editor.impl.ControlUi.getSession(ControlUi.java:346)
	at com.vaadin.server.communication.PushHandler$1.run(PushHandler.java:83)
	at com.vaadin.server.communication.PushHandler.callWithUi(PushHandler.java:240)
	at com.vaadin.server.communication.PushHandler.onConnect(PushHandler.java:483)
	at com.vaadin.server.communication.PushAtmosphereHandler.onConnect(PushAtmosphereHandler.java:99)
	at com.vaadin.server.communication.PushAtmosphereHandler.onRequest(PushAtmosphereHandler.java:75)
	at org.atmosphere.cpr.AsynchronousProcessor.action(AsynchronousProcessor.java:199)
	at org.atmosphere.cpr.AsynchronousProcessor.suspended(AsynchronousProcessor.java:107)
	at org.atmosphere.container.Jetty9AsyncSupportWithWebSocket.service(Jetty9AsyncSupportWithWebSocket.java:180)
	at org.atmosphere.cpr.AtmosphereFramework.doCometSupport(AtmosphereFramework.java:2075)
	at org.atmosphere.websocket.DefaultWebSocketProcessor.dispatch(DefaultWebSocketProcessor.java:571)
	at org.atmosphere.websocket.DefaultWebSocketProcessor.open(DefaultWebSocketProcessor.java:215)
	at org.atmosphere.container.Jetty9WebSocketHandler.onWebSocketConnect(Jetty9WebSocketHandler.java:110)
	at org.eclipse.jetty.websocket.common.events.JettyListenerEventDriver.onConnect(JettyListenerEventDriver.java:87)
	at org.eclipse.jetty.websocket.common.events.AbstractEventDriver.openSession(AbstractEventDriver.java:227)
	at org.eclipse.jetty.websocket.common.WebSocketSession.open(WebSocketSession.java:421)
	at org.eclipse.jetty.websocket.server.WebSocketServerConnection.onOpen(WebSocketServerConnection.java:72)
	at org.eclipse.jetty.io.AbstractEndPoint.upgrade(AbstractEndPoint.java:185)
	at org.eclipse.jetty.server.HttpConnection.completed(HttpConnection.java:345)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:436)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:257)
	at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:544)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:635)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:555)
	at java.lang.Thread.run(Thread.java:745)
2017-01-30T14:44:52 WARN : de.mhus.lib.core.logging.MLogUtil [672]

	 */
}
