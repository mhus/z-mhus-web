package de.mhus.cherry.portal.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.Acl;
import de.mhus.cherry.portal.api.CacheApi;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.Container;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.NavNode.TYPE;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.impl.deploy.CherryDeployServlet;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoUtil;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.CreateConfiguration;
import de.mhus.lib.cao.auth.AuthAccess;
import de.mhus.lib.cao.auth.AuthNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AaaUtil;
import de.mhus.osgi.sop.api.aaa.AccessApi;

@Component
public class CherryApiImpl extends MLog implements CherryApi {

	public static CherryApiImpl instance;
	private ThreadLocal<CallContext> calls = new ThreadLocal<>();
	
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
	public DeployDescriptor getDeployDescritor(Bundle bundle) {
		return CherryDeployServlet.instance.getDescriptor(bundle.getSymbolicName());
	}

	@Override
	public String getRecursiveString(CaoNode resource, String name) {
		CacheApi cache = Sop.getApi(CacheApi.class);
		Container val = cache.get(resource, "cherry_recursice_string_" + name);
		if (val != null) return val.getString();
		
		CaoNode r = resource;
		while (true) {
			if (r == null) {
				cache.put(resource, "cherry_recursice_string_" + name, null);
				return null;
			}
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

	@Override
	public CallContext getCurrentCall() {
		return calls.get();
	}

	public void setCallContext(CherryCallContext callContext) {
		if (callContext != null)
			calls.set(callContext);
		else
			calls.remove();
	}

	@Override
	public boolean deleteNavNode(CaoNode nav) {
		CaoNode parent = nav.getParent();
		if (parent == null) return false; // can't delete root
		
		// TODO inform all widgets and page to delete foreign resources?
		
		try {
			CaoUtil.deleteRecursive(nav, 100);
			return true;
		} catch (Throwable t) {
			log().d(t);
		}

		return false;

	}
	
	@Override
	public NavNode createNavNode(VirtualHost vHost, CaoNode parent, String pageRendition, String name, String title) throws CaoException {
		
		CaoNode newNav = null;
		CaoNode newRes = null;
		{	// Create Nav Node
			CaoAction action = parent.getConnection().getAction(CaoAction.CREATE);
			CaoConfiguration config = action.createConfiguration(parent, null);
			config.getProperties().setString(CreateConfiguration.NAME, name);
			config.getProperties().setString(CherryApi.NAV_TITLE, title);
			config.getProperties().setBoolean(CherryApi.NAV_HIDDEN, true);
			OperationResult result = action.doExecute(config, null);
			if (!result.isSuccessful()) return null;
			newNav = result.getResultAs(CaoNode.class);
		}
		{	// Create Res Node
			if (pageRendition == null) pageRendition = CherryApi.NAV_CONTENT_NODE;
			if (!pageRendition.startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX)) pageRendition = CherryApi.NAV_CONTENT_NODE_PREFIX + pageRendition;
			CaoAction action = parent.getConnection().getAction(CaoAction.CREATE);
			CaoConfiguration config = action.createConfiguration(newNav, null);
			config.getProperties().setString(CreateConfiguration.NAME, pageRendition);
			config.getProperties().setBoolean(CherryApi.NAV_HIDDEN, true);
			OperationResult result = action.doExecute(config, null);
			if (!result.isSuccessful()) return null;
			newRes = result.getResultAs(CaoNode.class);
		}

		return new NavNode(vHost.getNavigationProvider(), newNav, newRes, newRes, TYPE.NAVIGATION);
	}

	@Override
	public boolean hasResourceAccess(CaoNode node, String aclName) {
		AccessApi aaa = Sop.getApi(AccessApi.class);
		AaaContext context = aaa.getCurrentOrGuest();
		return hasResourceAccess(context, node, aclName);
	}

	@Override
	public boolean hasResourceAccess(AaaContext context, CaoNode node, String aclName) {
		
		String acl = getRecursiveString(node, "acl:" + aclName);
		if (acl == null) return true;
		return AaaUtil.hasAccess(context.getAccount(), acl);
		
	}
	
	@Override
	public Map<String, Acl> getEffectiveAcls(CaoNode node) {
		HashMap<String,Acl> out = new HashMap<>();
		int len = CherryApi.ACL_PREFIX.length();
		while(node != null) {
			for (String key : node.getPropertyKeys()) {
				if (key.startsWith(CherryApi.ACL_PREFIX)) {
					String aclName = key.substring(len);
					if (!out.containsKey(aclName)) {
						out.put(aclName, new Acl(node, key, node.getString(key, "")));
					}
				}
			}
			node = node.getParent();
		}
		return out;
	}
	
	@Override
	public CaoNode getAclDefiningNode(CaoNode node, String aclName) {
		String acl = CherryApi.ACL_PREFIX + aclName;
		while(node != null) {
			for (String key : node.getPropertyKeys()) {
				if (node.containsKey(acl)) return node;
			}
		}
		return null;
	}
	
	
}
