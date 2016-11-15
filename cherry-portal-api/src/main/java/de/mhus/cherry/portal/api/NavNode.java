package de.mhus.cherry.portal.api;

import java.util.Collection;
import java.util.LinkedList;

import de.mhus.lib.cao.CaoNode;

public class NavNode {

	public enum TYPE {NAVIGATION, PAGE, WIDGET, RESOURCE}
	private CaoNode nav;
	private CaoNode res;
	private NavigationProvider provider;
	private TYPE type = TYPE.NAVIGATION;

	public NavNode(NavigationProvider provider, CaoNode nav, CaoNode res, TYPE type) {
		this.provider = provider;
		this.nav = nav;
		this.res = res;
		this.type = type;
	}
	
	public CaoNode getNav() {
		return nav;
	}

	public CaoNode getRes() {
		return res;
	}

	public String getId() {
		if (type == TYPE.NAVIGATION) 
			return nav.getId();
		return res.getId();
	}

	public Collection<NavNode> getNodes() {
		if (type == TYPE.NAVIGATION)
			return provider.getChildren(this);
		LinkedList<NavNode> nodeChildren = new LinkedList<>();
		for (CaoNode n : res.getNodes())
			nodeChildren.add(new NavNode(provider, nav, n, type == TYPE.PAGE ? TYPE.WIDGET : TYPE.RESOURCE  ));
		return nodeChildren;
	}

	public Collection<NavNode> getAllNodes() {
		return provider.getAllChildren(this);
	}

	public TYPE getType() {
		return type;
	}
	
}
