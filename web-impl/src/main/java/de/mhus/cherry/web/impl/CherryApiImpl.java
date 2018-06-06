package de.mhus.cherry.web.impl;

import java.util.HashMap;

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
import de.mhus.cherry.web.api.SessionContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.util.TimeoutMap;
import de.mhus.osgi.services.util.MServiceTracker;

@Component
public class CherryApiImpl extends MLog implements CherryApi {

	private static CherryApiImpl instance;
	private ThreadLocal<CallContext> calls = new ThreadLocal<>();
	private TimeoutMap<String, SessionContext> globalSession = new TimeoutMap<>();
	private HashMap<String,VirtualHost> vHosts = new HashMap<>();
	
	MServiceTracker<VirtualHost> vHostTracker = new MServiceTracker<VirtualHost>(VirtualHost.class) {
		
		@Override
		protected void removeService(ServiceReference<VirtualHost> reference, VirtualHost service) {
			removeVirtualHost(service);
		}
		
		@Override
		protected void addService(ServiceReference<VirtualHost> reference, VirtualHost service) {
			addVirtualHost(service);
		}
	};

	public static CherryApiImpl instance() {
		return instance;
	}

	protected void addVirtualHost(VirtualHost service) {
		synchronized (vHosts) {
			service.start(this);
			VirtualHost old = vHosts.put(service.getVirtualHostAlias(), service);
			if (old != null)
				old.stop(this);
		}
	}

	protected void removeVirtualHost(VirtualHost service) {
		synchronized (vHosts) {
			vHosts.remove(service.getVirtualHostAlias());
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
		CallContext call = getCurrentCall();
		if (call != null)
			return call.getMimeType(file);
		String extension = MFile.getFileSuffix(file);
		return MFile.getMimeType(extension);
	}

	public SessionContext getCherrySession(String sessionId) {
		SessionContext ret = globalSession.get(sessionId);
		if (ret == null) {
			ret = new CherrySessionContext();
			globalSession.put(sessionId, ret);
		}
		return ret;
	}

	public void setCallContext(CherryCallContext callContext) {
		if (callContext != null)
			calls.set(callContext);
		else
			calls.remove();
	}

	public CallContext createCall(HttpServlet servlet, HttpServletRequest request,
	        HttpServletResponse response) {
		
		String host = request.getHeader("Host");
		VirtualHost vHost = findVirtualHost(host);
		
		CherryCallContext call = new CherryCallContext();
		call.setHttpRequest(request);
		call.setHttpResponse(response);
		call.setHttpServlet(servlet);
		call.setVirtualHost(vHost);
		
		return call;
	}

}
