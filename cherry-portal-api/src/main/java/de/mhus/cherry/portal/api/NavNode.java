package de.mhus.cherry.portal.api;

import java.util.Collection;
import java.util.LinkedList;

import de.mhus.lib.cao.CaoNode;

public class NavNode {

	public enum TYPE {NAVIGATION, PAGE, RESOURCE}
	private CaoNode nav;
	private CaoNode res;
	private NavigationProvider provider;
	private TYPE type = TYPE.NAVIGATION;
	private CaoNode main;

	public NavNode(NavigationProvider provider, CaoNode nav, CaoNode main, CaoNode res, TYPE type) {
		this.provider = provider;
		this.nav = nav;
		this.res = res;
		this.main = main;
		this.type = type;
	}
	
	public CaoNode getNav() {
		return nav;
	}

	public CaoNode getRes() {
		return res;
	}

	public CaoNode getMainRes() {
		return main;
	}
	
	public String getId() {
		if (type == TYPE.NAVIGATION) 
			return nav.getId();
		return res.getId();
	}

	public Collection<NavNode> getNodes() {
		return provider.getChildren(this);
	}

	public Collection<NavNode> getAllNodes() {
		return provider.getAllChildren(this);
	}

	public TYPE getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return type + " " + nav + " " + res;
	}

	public CaoNode getCurrent() {
		if (type == TYPE.NAVIGATION)
			return nav;
		return res;
	}
	
}
