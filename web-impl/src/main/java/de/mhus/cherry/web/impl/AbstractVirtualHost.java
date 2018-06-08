package de.mhus.cherry.web.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryActiveArea;
import de.mhus.cherry.web.api.CherryFilter;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public abstract class AbstractVirtualHost extends MLog implements VirtualHost {

	protected boolean traceErrors;
	protected boolean traceAccess;
	protected String[] aliases;
	protected String name = getClass().getCanonicalName();
	protected IConfig config;
	private MProperties properties = new MProperties();
	private Bundle bundle;
	protected LinkedList<CherryFilter> filters = new LinkedList<>();
	protected LinkedList<CherryFilter> filtersReverse = new LinkedList<>();
	protected LinkedList<ActiveAreaContainer> areas = new LinkedList<>();
	protected HashMap<String, String> headers = new HashMap<>();
	protected String defaultMimeType = MFile.DEFAULT_MIME;
	
	@Override
	public void sendError(CallContext context, int sc) {
		if (traceAccess)
			log().i(name,context.getHttpHost(),"error",context.getHttpRequest().getRemoteAddr(),context.getHttpMethod(),context.getHttpPath(),sc);
		if (traceErrors)
			log().i(name,context.getHttpHost(),sc,Thread.currentThread().getStackTrace());
		if (context.getHttpResponse().isCommitted()) {
			log().w("Can't send error to committed content",name,sc);
			return;
		}
		try {
			context.getHttpResponse().sendError(sc);
		} catch (IOException e) {
			log().t(e);
		}
	}

	/**
	 * Serve and distribute http requests.
	 * 
	 * See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html
	 * 
	 * @param context
	 */
	@Override
	public void doRequest(CallContext context) {
		
		try {
			CherryActiveArea area = findArea(context.getHttpPath());
			if (area != null) {
				area.doRequest(context);
				return;
			}
			
			String method = context.getHttpMethod();
			if (traceAccess)
				log().i("access",name,context.getHttpRequest().getRemoteAddr(),method,context.getHttpPath());
			
			for (Entry<String, String> entry : headers.entrySet())
				context.getHttpResponse().setHeader(entry.getKey(), entry.getValue());
			
			switch (method) {
			case "get":
				doGetRequest(context);
				break;
			case "head":
				doHeadRequest(context);
				break;
			case "post":
				doPostRequest(context);
				break;
			case "put":
				doPutRequest(context);
				break;
			case "delete":
				doDeleteRequest(context);
				break;
			case "options":
				doOptionsRequest(context);
				break;
			case "trace":
				doTraceRequest(context);
				break;
			case "connect":
				doConnectRequest(context);
				break;
			default:
				log().w("Unknown http method",name,method);
			}
		} catch (Throwable t) {
			sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doConnectRequest(CallContext context) throws Exception {
	}

	/**
	 * Send back the header values
	 * 
	 * @param context
	 */
	protected void doTraceRequest(CallContext context) throws Exception {
		HttpServletRequest req = context.getHttpRequest();
		HttpServletResponse res = context.getHttpResponse();
		for (Enumeration<String> en = req.getHeaderNames(); en.hasMoreElements();) {
			String name = en.nextElement();
			res.setHeader(name, req.getHeader(name));
		}
	}

	protected void doOptionsRequest(CallContext context) throws Exception {
		HttpServletResponse res = context.getHttpResponse();
		res.setHeader("cherry-name", context.getVirtualHost().getName());
		res.setHeader("Allow", config.getString("allow", "GET,HEAD,POST,PUT,DELETE,OPTIONS,TRACE"));
	}

	protected abstract void doDeleteRequest(CallContext context) throws Exception;

	protected abstract void doPutRequest(CallContext context) throws Exception;

	protected abstract void doPostRequest(CallContext context) throws Exception;
	
	protected abstract void doHeadRequest(CallContext context) throws Exception;

	protected abstract void doGetRequest(CallContext context) throws Exception;

	@Override
	public IConfig getConfig() {
		return config;
	}

	@Override
	public IProperties getProperties() {
		return properties;
	}

	@Override
	public String[] getVirtualHostAliases() {
		return aliases;
	}

	@Override
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Bundle getBundle() {
		return bundle;
	}

	@Override
	public boolean doFiltersBegin(CallContext call) throws MException {
		if (filters == null || filters.size() == 0) return true;
		// do not synchronize - it's to slow
		for (CherryFilter filter : filters) {
			if (!filter.doFilterBegin(call)) return false;
		}
		return true;
	}

	@Override
	public void doFiltersEnd(CallContext call) throws MException {
		if (filters == null || filters.size() == 0) return;
		// do not synchronize - it's to slow
		for (CherryFilter filter : filtersReverse) {
			filter.doFilterEnd(call);
		}
	}

	public CherryActiveArea findArea(String path) {
		if (areas == null || areas.size() == 0) return null;
		// do not synchronize - it's to slow
		for (ActiveAreaContainer area : areas)
			if (path.startsWith(area.alias)) return area.area;
		return null;
	}

	public void addFilter(CherryFilter filter) {
		filters.add(filter);
		filtersReverse.add(0,filter);
	}
	
	public void addArea(String alias, CherryActiveArea area) {
		areas.add(new ActiveAreaContainer(alias, area));
	}
	
	protected class ActiveAreaContainer {
		public ActiveAreaContainer(String alias, CherryActiveArea area) {
			this.area = area;
			this.alias = alias;
		}
		String alias;
		CherryActiveArea area;
	}

	@Override
	public String getMimeType(String file) {
		String extension = MFile.getFileSuffix(file);
		return MFile.getMimeType(extension, defaultMimeType);
	}

	@Override
	public String getName() {
		return name;
	}

}
