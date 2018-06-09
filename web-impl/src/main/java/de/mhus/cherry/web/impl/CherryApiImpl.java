package de.mhus.cherry.web.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.api.Session;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.services.util.MServiceTracker;
import de.mhus.osgi.sop.api.security.SecurityApi;

@Component(immediate=true)
public class CherryApiImpl extends MLog implements CherryApi {

	public static final String SESSION_PARAMETER_SESSION = "__cherry_global_session";
	
	private static CherryApiImpl instance;
	private ThreadLocal<CallContext> calls = new ThreadLocal<>();
	private WeakHashMap<String, Session> globalSession = new WeakHashMap<>();
	private HashMap<String,VirtualHost> vHosts = new HashMap<>();
	
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
			try {
				service.start(this);
			} catch (Throwable t) {
				log().e("Can't add virtual host",service.getName(), t);
				return;
			}
			String[] aliases = service.getVirtualHostAliases();
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
		vHostTracker.start();
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
			VirtualHost vHost = vHosts.get(host);
			if (vHost == null) {
				vHost = vHosts.get("*");
			}
			return vHost;
		}
	}

	@Override
	public String getMimeType(String file) {
		String extension = MFile.getFileSuffix(file);
		return MFile.getMimeType(extension);
	}

	public boolean isCherrySession(String sessionId) {
		Session ret = globalSession.get(sessionId);
		return ret != null;
	}
	
	public Session getCherrySession(CallContext context, String sessionId) {
		Session ret = globalSession.get(sessionId);
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
	public CallContext createCallContext(HttpServlet servlet, HttpServletRequest request,
	        HttpServletResponse response) throws MException {
		
		// check general security
		SecurityApi sec = MApi.lookup(SecurityApi.class);
		if (sec != null) {
			sec.checkHttpRequest(request, response);
			if (response.isCommitted()) return null;
		}

		// find vhost
		String host = request.getHeader("Host");
		VirtualHost vHost = findVirtualHost(host);
		if (vHost == null) return null;
		
		// create call context
		CherryCallContext call = new CherryCallContext();
		call.setHttpRequest(request);
		call.setHttpResponse(response);
		call.setHttpServlet(servlet);
		call.setVirtualHost(vHost);

		return call;
	}

	public LinkedList<VirtualHost> getVirtualHosts() {
		return new LinkedList<>(vHosts.values());
	}

}
