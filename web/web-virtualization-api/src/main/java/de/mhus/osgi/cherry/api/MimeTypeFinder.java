package de.mhus.osgi.cherry.api;

import de.mhus.lib.core.directory.ResourceNode;

public interface MimeTypeFinder {

	String getMimeType(ResourceNode res);
	
	String getMimeType(String res);
	
}
