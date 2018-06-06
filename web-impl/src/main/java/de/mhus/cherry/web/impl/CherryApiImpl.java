package de.mhus.cherry.web.impl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import de.mhus.lib.errors.NotFoundException;
import de.mhus.osgi.services.MOsgi;

@Component
public class CherryApiImpl extends MLog implements CherryApi {

	private static CherryApiImpl instance;
	private ThreadLocal<CallContext> calls = new ThreadLocal<>();
	public TimeoutMap<String, SessionContext> globalSession = new TimeoutMap<>();

	public static CherryApiImpl instance() {
		return instance;
	}

	@Activate
	public void doActivate(ComponentContext ctx) {
		log().i("Start Cherry");
		instance = this;
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		instance = null;
	}

	@Override
	public CallContext getCurrentCall() {
		return calls.get();
	}

	public VirtualHost findVirtualHost(String host) {
		VirtualHost provider = null;
		try {
			provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_" + host));
		} catch (NotFoundException e) {}
		if (provider == null) {
			try {
				provider = MOsgi.getService(VirtualHost.class, MOsgi.filterServiceName("cherry_virtual_host_default"));
			} catch (NotFoundException e) {}
		}
		
		return provider;
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
