package de.mhus.cherry.portal.api;

import java.util.Map;

import de.mhus.lib.cao.CaoNode;

public interface ContentNodeResolver {

	/**
	 * Find the current active page for a navigation node.
	 * 
	 * @param nav Navigation Node
	 * @return The current active page for this nav node
	 */
	CaoNode doResolve(CaoNode nav);

	/**
	 * Returns a parameter from navigation tree using
	 * alternative for the selected page and
	 * fallback mechanisms if not set.
	 * 
	 * @param nav
	 * @param name
	 * @return
	 */
	String getRecursiveString(NavNode nav, String name);

	/**
	 * Returns a list of alternatives for the given name at
	 * the given navigation node. The map contains a display name
	 * as value.
	 * 
	 * @param nav
	 * @param name
	 * @param caption The caption of the attribute
	 * @return
	 */
	Map<String,String> getAlternatives(CaoNode nav, String name, String caption);

	/**
	 * Returns a set of default page possibilities.
	 * The map contains a display name as value.
	 * @return
	 */
	Map<String,String> getDefaultPages();
	
}
