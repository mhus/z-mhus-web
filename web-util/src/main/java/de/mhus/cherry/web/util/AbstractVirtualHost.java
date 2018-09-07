package de.mhus.cherry.web.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebArea;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;

public abstract class AbstractVirtualHost extends MLog implements VirtualHost {

	public static final String CALL_FILTER_CNT = "__call_filter_cnt";
	
	protected boolean traceErrors;
	protected boolean traceAccess;
	private Set<String> aliases;
	protected String name = getClass().getCanonicalName();
	protected IConfig config;
	private MProperties properties = new MProperties();
	private Bundle bundle;
	protected LinkedList<WebFilter> filters = new LinkedList<>();
	protected LinkedList<WebFilter> filtersReverse = new LinkedList<>();
	protected LinkedList<ActiveAreaContainer> areas = new LinkedList<>();
	protected HashMap<String, String> headers = new HashMap<>();
	protected String defaultMimeType = MFile.DEFAULT_MIME;
	protected String charsetEncoding = MString.CHARSET_UTF_8;

	private String[] externalAliases;
	private String firstAlias; // use as name default
	private String profile;
	
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
	 * @param call
	 */
	@Override
	public void doRequest(InternalCallContext call) {
		try {
			
			// execute filters
			if (!doFiltersBegin(call)) 
				return;
			
			if (doActiveAreas(call))
				return;
			
			String method = call.getHttpMethod();
			if (traceAccess)
				log().i("access",name,call.getHttpRequest().getRemoteAddr(),method,call.getHttpPath());
			
			for (Entry<String, String> entry : headers.entrySet())
				call.getHttpResponse().setHeader(entry.getKey(), entry.getValue());
			
			switch (method) {
			case "get":
				doGetRequest(call);
				break;
			case "head":
				doHeadRequest(call);
				break;
			case "post":
				doPostRequest(call);
				break;
			case "put":
				doPutRequest(call);
				break;
			case "delete":
				doDeleteRequest(call);
				break;
			case "options":
				doOptionsRequest(call);
				break;
			case "trace":
				doTraceRequest(call);
				break;
			case "connect":
				doConnectRequest(call);
				break;
			default:
				log().w("Unknown http method",name,method);
			}
		} catch (Throwable t) {
			sendError(call, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (call != null) {
					doFiltersEnd(call);
				}
			} catch (Throwable t) {
				MLogUtil.log().w(t);
			}
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

	/**
	 * Set a comma separated list of aliases. Use this in a blueprint.
	 * @param aliases
	 */
	public void setAliases(String aliases) {
		this.externalAliases = aliases.split(",");
	}
	
	protected void setConfigAliases(String[] aliases) {
		// merge external configured aliases with internal aliases
		LinkedList<String> a = new LinkedList<>();
		// external configuration
		if (externalAliases != null)
			MCollection.addAll(a, externalAliases);
		// configured aliases
		if (aliases != null)
			MCollection.addAll(a, aliases);
		// set
		this.aliases = new HashSet<>(a);
		this.firstAlias = a.size() == 0 ? "?" : a.getFirst();
	}
	
	public String getFirstAlias() {
		return firstAlias;
	}
	
	@Override
	public Set<String> getVirtualHostAliases() {
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

	public boolean doFiltersBegin(InternalCallContext call) throws MException {
		if (filters == null || filters.size() == 0) return true;
		// do not synchronize - it's to slow
		int cnt = 0; // count number of filters executed
		for (WebFilter filter : filters) {
			if (!filter.doFilterBegin(call)) {
				call.setAttribute(CALL_FILTER_CNT, cnt);
				return false;
			}
			cnt++;
		}
		call.setAttribute(CALL_FILTER_CNT, cnt);
		return true;
	}

	public void doFiltersEnd(InternalCallContext call) throws MException {
		if (filters == null || filters.size() == 0) return;
		// do not synchronize - it's to slow
		int cnt = filtersReverse.size();
		int done = (int) call.getAttribute(CALL_FILTER_CNT);
		for (WebFilter filter : filtersReverse) {
			if (cnt <= done) // do only end for filter they had begin called, expect the one returned false
				filter.doFilterEnd(call);
			cnt--;
		}
	}

	public boolean doActiveAreas(InternalCallContext call) throws MException {
		if (areas == null || areas.size() == 0) return false;
		String path = call.getHttpPath();
		// do not synchronize - it's to slow
		for (ActiveAreaContainer area : areas)
			if (path.startsWith(area.alias)) {
				if (area.area.doRequest(call))
					return true;
			}
		return false;
	}

	public void addFilter(WebFilter filter) {
		filters.add(filter);
		filtersReverse.add(0,filter);
	}
	
	public void addArea(String alias, WebArea area) {
		areas.add(new ActiveAreaContainer(alias, area));
	}
	
	protected class ActiveAreaContainer {
		public ActiveAreaContainer(String alias, WebArea area) {
			this.area = area;
			this.alias = alias;
		}
		String alias;
		WebArea area;
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

	@Override
	public String getCharsetEncoding() {
		return charsetEncoding;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	/**
	 * combine the name of the config file or section with the current profile name. If
	 * profile is not set the name itself will be returned.
	 * 
	 * @param name Name of the config or section
	 * @return Combined name with profile
	 */
	public String prepareConfigName(String name) {
		if (MString.isEmpty(profile)) return name;
		return MFile.normalize(profile) + "_" + name;
	}

}
