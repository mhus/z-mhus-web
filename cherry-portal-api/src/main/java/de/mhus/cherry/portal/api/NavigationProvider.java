package de.mhus.cherry.portal.api;

import java.util.Collection;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;

public interface NavigationProvider {

	/**
	 * Return only navigation resources
	 */
	public NavNode getNode(String path);

	/**
	 * Return all navigation children, pages will be ignored
	 * 
	 * @param navNode
	 * @return
	 */
	public Collection<NavNode> getChildren(NavNode navNode);

	public CaoNode getResource(String resId);

	public String getName();

	/**
	 * Return navigation pages and resources in deeper structures.
	 * The method will also follow references.
	 * 
	 * @param navNode
	 * @return
	 */
	public Collection<NavNode> getAllChildren(NavNode navNode);
	
	Change[] getChanges();
	
}
