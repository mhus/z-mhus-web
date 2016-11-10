package de.mhus.cherry.portal.impl;

import java.util.Collection;
import java.util.LinkedList;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ContentNodeResolver;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.osgi.sop.api.Sop;

public class DefaultNavigationProvider extends MLog implements NavigationProvider {

	private CaoConnection connection;
	private VirtualHost vHost;
	
	public DefaultNavigationProvider(VirtualHost vHost) {
		this.vHost = vHost;
	}
	
	@Override
	public NavNode getNode(String path) {
		
		if (MString.isIndex(path, '.'))
			path = MString.beforeIndex(path, '.');
		
		CaoNode nav = connection.getResourceByPath(path);
		if (nav == null) return null;
		
		return  prepare(nav);
	}

	private NavNode prepare(CaoNode nav) {
		CaoNode res = null;
		ContentNodeResolver resolver = vHost.getContentNodeResolver();
		if (resolver != null) {
			res = resolver.doResolve(nav);
		} else {
			log().d("ContentNodeResolver not found");
		}
		return new NavNode(this, nav, res);
	}

	public CaoConnection getConnection() {
		return connection;
	}

	public void setConnection(CaoConnection connection) {
		this.connection = connection;
	}

	@Override
	public Collection<NavNode> getChildren(NavNode parent) {
		LinkedList<NavNode> out = new LinkedList<>();
		for (CaoNode node : parent.getNav().getNodes())
			if (!node.getName().startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX))
				out.add( prepare(node) );
		return out;
	}

	@Override
	public CaoNode getResource(String resId) {
		if (resId == null) return null;
		if (resId.startsWith("/"))
			return connection.getResourceByPath(resId);
		else
			return connection.getResourceById(resId);
	}

	@Override
	public String getName() {
		return connection.getName();
	}

}
