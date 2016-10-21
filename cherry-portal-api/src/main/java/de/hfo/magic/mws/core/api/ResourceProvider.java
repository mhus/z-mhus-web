package de.hfo.magic.mws.core.api;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.directory.IResourceProvider;

public interface ResourceProvider extends IResourceProvider {

	@Override
	CaoNode getResourceByPath(String path);
	
	@Override
	CaoNode getResourceById(String path);

}
