package de.mhus.cherry.portal.api;

import java.util.Collection;
import java.util.LinkedList;

import de.mhus.lib.cao.CaoNode;

public class NavNode {

	private CaoNode nav;
	private CaoNode res;
	private NavigationProvider provider;
	private boolean deep = false;

	public NavNode(NavigationProvider provider, CaoNode nav, CaoNode res, boolean deep) {
		this.provider = provider;
		this.nav = nav;
		this.res = res;
		this.deep = deep;
	}
	
	public CaoNode getNav() {
		return nav;
	}

	public CaoNode getRes() {
		return res;
	}

	public String getId() {
		if (deep && res != null) 
			return res.getId();
		return nav.getId();
	}

	public Collection<NavNode> getNodes() {
		return provider.getChildren(this);
	}

	public Collection<NavNode> getAllNodes() {
		return provider.getAllChildren(this);
	}

	public boolean isDeep() {
		return deep;
	}

	
}
