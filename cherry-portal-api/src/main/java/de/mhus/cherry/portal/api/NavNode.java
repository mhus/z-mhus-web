package de.mhus.cherry.portal.api;

import java.util.Collection;
import java.util.LinkedList;

import de.mhus.lib.cao.CaoNode;

public class NavNode {

	private CaoNode nav;
	private CaoNode res;
	private NavigationProvider provider;

	public NavNode(NavigationProvider provider, CaoNode nav, CaoNode res) {
		this.provider = provider;
		this.nav = nav;
		this.res = res;
	}
	
	public CaoNode getNav() {
		return nav;
	}

	public CaoNode getRes() {
		return res;
	}

	public String getId() {
		return nav.getId();
	}

	public Collection<NavNode> getNodes() {
		return provider.getChildren(this);
	}
	
}
