package de.mhus.cherry.portal.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.basics.Named;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.cfg.CfgBoolean;
import de.mhus.lib.core.cfg.CfgInt;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.util.TimeoutMap;
import de.mhus.lib.core.util.TimeoutMap.Invalidator;
import de.mhus.lib.servlet.HttpServletRequestWrapper;
import de.mhus.lib.servlet.HttpServletResponseWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.security.SecurityApi;

@Component
public class InternalCherryApiImpl extends MLog implements InternalCherryApi, BundleListener {

	private static CfgBoolean storePassword = new CfgBoolean(CherryApi.class, "storePasswordInSession", false);
	private static CfgLong sessionTimeout = new CfgLong(CherryApi.class, "sessionTimeout", MTimeInterval.MINUTE_IN_MILLISECOUNDS * 20);
	private static CfgInt sessionMaxSize = new CfgInt(CherryApi.class, "sessionMaxSize", 100);
	
	public TimeoutMap<String, MProperties> globalSession = new TimeoutMap<>();
	public static InternalCherryApiImpl instance = null;
	public HashMap<String, IProperties> bundleStore = new HashMap<>();
	
    @Activate
    public void activate(ComponentContext ctx) {
    	ctx.getBundleContext().addBundleListener(this);
    	instance = this;
    	globalSession.setInvalidator(new Invalidator<String, MProperties>() {

			@Override
			public boolean isInvalid(String key, MProperties value, long time, long accessed) {
				return 
					System.currentTimeMillis() - time > sessionTimeout.value() 
					||
					globalSession.size() > sessionMaxSize.value() && accessed < 2 && System.currentTimeMillis() - time > 10000;
			}
		});
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	ctx.getBundleContext().removeBundleListener(this);
    	instance = null;
    }

	@Override
	public CallContext createCall(HttpServlet servlet, HttpServletRequest req, HttpServletResponse res) throws IOException {

		// check general security
		SecurityApi sec = Sop.getApi(SecurityApi.class, false);
		sec.checkHttpRequest(req, res);
		if (res.isCommitted()) return null;
		
		CherryCallContext callContext = new CherryCallContext();
		callContext.setHttpRequest(req);
		callContext.setHttpResponse(new CherryResponseWrapper(res));
		callContext.setHttpServlet(servlet);

		// find virtual host
		String host = req.getHeader("Host");
		VirtualHost vHost = CherryApiImpl.instance.findVirtualHost(host);
		if (vHost == null) {
			res.sendError(HttpServletResponse.SC_BAD_GATEWAY);
			return callContext; // is commited ?
		}
		callContext.setVirtualHost(vHost);
		
		// check access
		// check host specific security
		// 1) host access general
		{
			List<String> list = vHost.getConfigurationList(CherryApi.CONFIG_HOST_ALLOWED);
			if (list != null) {
				boolean found = false;
				for (String item : list)
					if (host.matches(item)) {
						found = true;
						break;
					}
				if (!found) {
					vHost.sendError(callContext, HttpServletResponse.SC_NOT_FOUND);
					return null;
				}
			}
		}
		// 2) Check access for this servlet
		if (servlet instanceof Named) {
			List<String> list = vHost.getConfigurationList(CherryApi.CONFIG_HOST_ALLOWED + "_" + ((Named)servlet).getName());
			if (list != null) {
				boolean found = false;
				for (String item : list)
					if (host.matches(item)) {
						found = true;
						break;
					}
				if (!found) {
					vHost.sendError(callContext, HttpServletResponse.SC_NOT_FOUND);
					return null;
				}
			}
		}
		
		// find logged in user and auto login
        AccessApi access = Sop.getApi(AccessApi.class);
        AaaContext context = getContext( req.getSession().getId() );
        if (context == null) {
        	context = vHost.doLogin(new HttpServletRequestWrapper(req), new HttpServletResponseWrapper(res) );
        	if (res.isCommitted()) return callContext;
        }
        //callContext.setCurrentAaaContext(context);
        access.process(context);

        // remember the current call for this request
		CherryApiImpl.instance.setCallContext(callContext);
		
		// enable trail tracing
		{
			List<String> list = vHost.getConfigurationList(CherryApi.CONFIG_ACCOUNT_TRAIL_ENABLED);
			if (list != null && list.contains(context.getAccountId())) {
				MLogUtil.setTrailConfig();
			}
			log().d(">>>", context.getAccountId(), req.getPathInfo());
		}
		
		
		return callContext;
	}

	@Override
	public void releaseCall(CallContext call) {
		
        AccessApi access = Sop.getApi(AccessApi.class);
    	access.release(call.getAaaContext());
		CherryApiImpl.instance.setCallContext(null);

		if (call.getSession().getBoolean(SESSION_DESTROY_ON_RELEASE, false)) {
			globalSession.remove(call.getSessionId());
		}
		
		log().d("<<<", call.getAaaContext().getAccountId(), call.getHttpRequest().getPathInfo());
		MLogUtil.releaseTrailConfig();
		
	}

	@Override
	public synchronized IProperties getCherrySession(String sessionId) {
		MProperties ret = globalSession.get(sessionId);
		if (ret == null) {
			ret = new MProperties();
			globalSession.put(sessionId, ret);
		}
		return ret;
	}

	@Override
	public AaaContext getContext(String sessionId) {
		IProperties session = getCherrySession(sessionId);
		return (AaaContext) session.get(SESSION_ACCESS_NAME);
	}

	@Override
	public String doLogin(String username, String password) {
		CallContext call = CherryApiImpl.instance.getCurrentCall();
		IProperties session = call.getSession();
		if (session.get(SESSION_ACCESS_NAME) != null) {
			return "already logged in";
		}
		
		// for secure try to release
		AaaContext current = (AaaContext)session.get(SESSION_ACCESS_NAME);
		AccessApi api = Sop.getApi(AccessApi.class);
		if (current != null) {
			api.release(current);
		}
		
		AaaContext context = api.process(api.createUserTicket(username,password));
		if (context == null) {
			return "wrong user or password";
		}
		
		// cleanup session
		session.clear();

		// set new values
		session.put(SESSION_ACCESS_NAME, context);
		session.put(SESSION_ACCESS_USERNAME, username);
		if (storePassword.value())
			session.put(SESSION_ACCESS_PASSWORD, password);
		return null;
	}

	@Override
	public String doLogout() {
		CallContext call = CherryApiImpl.instance.getCurrentCall();
		IProperties session = call.getSession();
		if (session.get(SESSION_ACCESS_NAME) == null) {
			return "not logged in";
		}

		// cleanup session
		session.clear();
		//session.remove(SESSION_ACCESS_NAME);

		return null;
	}

	@Override
	public boolean isLoggedIn() {
		CallContext call = CherryApiImpl.instance.getCurrentCall();
		if (call == null) return false;
		IProperties session = call.getSession();
		return session.get(SESSION_ACCESS_NAME) != null;
	}

	@Override
	public IProperties getBundleStore(Bundle bundle) {
		synchronized (bundleStore) {
			IProperties ret = bundleStore.get(bundle.getSymbolicName());
			if (ret == null) {
				ret = new MProperties();
				bundleStore.put(bundle.getSymbolicName(), ret);
			}
			return ret;
		}
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.STOPPED || event.getType() == BundleEvent.UPDATED) {
			synchronized (bundleStore) {
				bundleStore.remove(event.getBundle().getSymbolicName());
			}
		}
	}

}
