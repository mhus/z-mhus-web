package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.directory.IResourceProvider;

public interface ResourceProvider {

	public CaoNode getResource(String id);

	String getName();

}
