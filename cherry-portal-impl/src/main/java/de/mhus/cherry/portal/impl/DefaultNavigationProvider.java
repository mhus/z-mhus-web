package de.mhus.cherry.portal.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ContentNodeResolver;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.NavNode.TYPE;
import de.mhus.cherry.portal.api.NavigationProvider;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoAspectFactory;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.Changes;
import de.mhus.lib.cao.util.DefaultChangesQueue;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;
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

		int pos = path.indexOf("/" + CherryApi.NAV_CONTENT_NODE_PREFIX); // minimum one page starting prefix
		if (pos >= 0) return null; // do not return deep, non nav, nodes

		CaoNode nav = connection.getResourceByPath(path);
		if (nav == null) return null;
		
		return  prepare(null, nav);
	}

	private NavNode prepare(NavNode parent, CaoNode node) {
		if (parent != null && parent.getType() != TYPE.NAVIGATION) {
			
			String ref = node.getString(CherryApi.REFERENCE_ID, null);
			if (ref != null) {
				ContentNodeResolver resolver = vHost.getContentNodeResolver();
				if (resolver != null) {
					node = resolver.doResolve(node);
				} else {
					log().d("ContentNodeResolver not found (2)");
				}
			}
			return new NavNode(this, parent.getNav(), parent.getMainRes(), node, TYPE.RESOURCE );
		}
		
//		if (path != null) {
//			int pos = path.indexOf("/" + CherryApi.NAV_CONTENT_NODE_PREFIX); // minimum one page starting prefix
//			if (pos >= 0) {
//				if (path.indexOf('/', pos+1 ) < 0) // no more slash after prefix
//					return new NavNode(this, parent.getNav(), node, node, TYPE.PAGE);
//				else
//					return new NavNode(this, parent.getNav(), parent.getMainRes(), node, TYPE.RESOURCE);
//			}
//		}
		
		if (node.getName().startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX)) {
			return new NavNode(this, parent.getNav(), node, node, TYPE.PAGE);
		}
		
		CaoNode res = null;
		ContentNodeResolver resolver = vHost.getContentNodeResolver();
		if (resolver != null) {
			res = resolver.doResolve(node);
		} else {
			log().d("ContentNodeResolver not found");
		}
		return new NavNode(this, node, res, res, TYPE.NAVIGATION);
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
		if (parent != null && parent.getType() == TYPE.NAVIGATION) {
			for (CaoNode node : parent.getNav().getNodes())
				if (!node.getName().startsWith(CherryApi.NAV_CONTENT_NODE_PREFIX))
					out.add( prepare(parent, node) );
		}
		out.sort( (NavNode a,NavNode b) -> Integer.compare(a.getCurrent().getInt(WidgetApi.SORT, -1), b.getCurrent().getInt(WidgetApi.SORT, -1)) );
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

	/**
	 * Return navigation and page nodes.
	 */
	@Override
	public Collection<NavNode> getAllChildren(NavNode parent) {
		LinkedList<NavNode> out = new LinkedList<>();
		for (CaoNode node : parent.getType() == TYPE.NAVIGATION ? parent.getNav().getNodes() : parent.getRes().getNodes())
			out.add( prepare(parent, node) );
		out.sort( (NavNode a,NavNode b) -> Integer.compare(a.getCurrent().getInt(WidgetApi.SORT, -1), b.getCurrent().getInt(WidgetApi.SORT, -1)) );
		return out;
	}

	@Override
	public Change[] getChanges() {
		if (connection == null) return null;
		CaoAspectFactory<Changes> factory = connection.getAspectFactory(Changes.class);
		if (factory != null && factory instanceof DefaultChangesQueue) {
			return ((DefaultChangesQueue)factory).clearEventQueue();
		}
		return null;
	}

}
