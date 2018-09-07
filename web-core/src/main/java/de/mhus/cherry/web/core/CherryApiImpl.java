package de.mhus.cherry.web.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebSession;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.cfg.CfgInt;
import de.mhus.lib.errors.MException;
import de.mhus.lib.servlet.security.SecurityApi;
import de.mhus.osgi.services.util.MServiceTracker;

@Component(immediate=true)
public class CherryApiImpl extends MLog implements CherryApi {

	public static final String SESSION_PARAMETER_SESSION = "__cherry_global_session";
	private static CfgInt CFG_MAX_VHOST_CACHE_SIZE = new CfgInt(CherryApi.class, "maxVHostCacheSize", 200);
	
	private static CherryApiImpl instance;
	private ThreadLocal<CallContext> calls = new ThreadLocal<>();
	private WeakHashMap<String, WebSession> globalSession = new WeakHashMap<>();
	private HashMap<String,VirtualHost> vHosts = new HashMap<>();
	private HashMap<String,VirtualHost> vHostsCache = new HashMap<>();
	
	MServiceTracker<VirtualHost> vHostTracker = new MServiceTracker<VirtualHost>(VirtualHost.class) {
		
		@Override
		protected void removeService(ServiceReference<VirtualHost> reference, VirtualHost service) {
			removeVirtualHost(service);
			service.setBundle(null);
		}
		
		@Override
		protected void addService(ServiceReference<VirtualHost> reference, VirtualHost service) {
			service.setBundle(reference.getBundle());
			addVirtualHost(service);
		}
	};

	public static CherryApiImpl instance() {
		return instance;
	}

	protected void addVirtualHost(VirtualHost service) {
		synchronized (vHosts) {
			vHostsCache.clear();
			try {
				service.start(this);
			} catch (Throwable t) {
				log().e("Can't add virtual host",service.getName(), t);
				return;
			}
			Set<String> aliases = service.getVirtualHostAliases();
			for (String alias : aliases) {
				log().i("add",alias);
				VirtualHost old = vHosts.put(alias, service);
				if (old != null)
					old.stop(this);
			}
		}
	}

	protected void removeVirtualHost(VirtualHost service) {
		synchronized (vHosts) {
			vHostsCache.clear();
			vHosts.entrySet().removeIf(e -> { 
				if (service == e.getValue()) {
					log().i("remove",e.getKey());
					return true;
				}
				return false;
				});
			service.stop(this);
		}
	}

	@Activate
	public void doActivate(ComponentContext ctx) {
		log().i("Start Cherry");
		instance = this;
		vHostTracker.start(ctx);
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		vHostTracker.stop();
		instance = null;
	}

	@Override
	public CallContext getCurrentCall() {
		return calls.get();
	}

	public VirtualHost findVirtualHost(String host) {
		synchronized (vHosts) {
			// get from cache
			VirtualHost vHost = null;
			vHost = vHostsCache.get(host);
			if (vHost != null) return vHost;
			
			// loopup
			vHost = vHosts.get(host);
			if (vHost == null) {
				// remove port
				String h = host;
				int p = h.indexOf(':');
				if (p > 0)
					h = h.substring(0, p) + ":*";
				else
					h = h + ":*";
				vHost = vHosts.get(h);
			}
			if (vHost == null) {
				vHost = vHosts.get("*");
			}
			// save to cache
			if (vHost != null && vHostsCache.size() < CFG_MAX_VHOST_CACHE_SIZE.value())
				vHostsCache.put(host, vHost);
			return vHost;
		}
	}

	@Override
	public String getMimeType(String file) {
		String extension = MFile.getFileSuffix(file);
		return MFile.getMimeType(extension);
	}

	public boolean isCherrySession(String sessionId) {
		WebSession ret = globalSession.get(sessionId);
		return ret != null;
	}
	
	public WebSession getCherrySession(CallContext context, String sessionId) {
		WebSession ret = globalSession.get(sessionId);
		if (ret == null) {
			if (context == null) return null;
			ret = new CherrySession(sessionId);
			globalSession.put(sessionId, ret);
			// put into http session to create a reference until http session time out
			context.getHttpRequest().getSession().setAttribute(SESSION_PARAMETER_SESSION, globalSession); 
		}
		return ret;
	}

	public void setCallContext(CherryCallContext callContext) {
		if (callContext != null)
			calls.set(callContext);
		else
			calls.remove();
	}

	@Override
	public InternalCallContext createCallContext(Servlet servlet, HttpServletRequest request,
	        HttpServletResponse response) throws MException {
		
		// check general security
		SecurityApi sec = MApi.lookup(SecurityApi.class);
		if (sec != null) {
			if (!sec.checkHttpRequest(request, response))
				return null;
			if (response.isCommitted()) return null;
		}

		// find vhost
		String host = request.getHeader("Host");
		VirtualHost vHost = findVirtualHost(host);
		if (vHost == null) return null;
		
		// create call context
		CherryCallContext call = new CherryCallContext(servlet, request, response, vHost);

		return call;
	}

	public Map<String, VirtualHost> getVirtualHosts() {
		return Collections.unmodifiableMap(vHosts);
	}

}
