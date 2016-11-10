package de.mhus.cherry.portal.api;

import java.util.Collection;

import de.mhus.lib.cao.CaoNode;

public interface NavigationProvider {

	public NavNode getNode(String path);

	public Collection<NavNode> getChildren(NavNode navNode);

	public CaoNode getResource(String resId);

	public String getName();

	public Collection<NavNode> getAllChildren(NavNode navNode);
	
}
