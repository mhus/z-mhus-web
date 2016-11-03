package de.mhus.cherry.portal.impl;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CacheApi;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.impl.deploy.CherryDeployServlet;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.auth.AuthAccess;
import de.mhus.lib.cao.auth.AuthNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.TimeoutMap;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.SopApi;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

@Component
public class CherryApiImpl extends MLog implements CherryApi {

	public static CherryApiImpl instance;
	
	public TimeoutMap<String, MProperties> globalSession = new TimeoutMap<>();
	
    @Activate
    public void activate(ComponentContext ctx) {
    	instance = this;
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	instance = null;
    }

	@Override
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
	public FileDeployer findFileDeployer(String suffix) {
		if (suffix == null) return null;
		suffix = suffix.toLowerCase();
		FileDeployer deployer = null;
		try {
			deployer = MOsgi.getService(FileDeployer.class, MOsgi.filterServiceName("cherry_file_deployer_" + suffix));
		} catch (NotFoundException e) {}
		return deployer;
	}

	@Override
	public String getMimeType(String file) {
		return CherryDeployServlet.instance.getServletContext().getMimeType(file);
	}

	@Override
	public DeployDescriptor getDeployDescritor(String symbolicName) {
		return CherryDeployServlet.instance.getDescriptor(symbolicName);
	}

	@Override
	public String getRecursiveString(CaoNode resource, String name) {
		CacheApi cache = Sop.getApi(CacheApi.class);
		String val = cache.getString(resource, "cherry_recursice_string_" + name);
		if (val != null) return val;
		
		CaoNode r = resource;
		while (true) {
			if (r == null) return null;
			String value = r.getString(name, null);
			if (value != null) {
				cache.put(resource, "cherry_recursice_string_" + name, value);
				return value;
			}
			r = r.getParent();
		}
	}

//	@Override
//	public IProperties getCherrySession(RequestWrapper request) {
//		String sessionId = request.getRequestedSessionId();
//		if (sessionId == null) {
//			request.getSession();
//			sessionId = request.getRequestedSessionId();
//			if (sessionId == null)
//				return null;
//		}
//		return getCherrySession(sessionId);
//	}

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
	public boolean canEditResource(CallContext call, CaoNode res) {
		// TODO check if editor is available
		// check if edit is on
		
		if (res instanceof AuthNode ) {
			AuthAccess access = res.adaptTo(AuthAccess.class);
			if (access.hasWriteAccess())
				return true;
			return false;
		}

		return true;
	}

}
