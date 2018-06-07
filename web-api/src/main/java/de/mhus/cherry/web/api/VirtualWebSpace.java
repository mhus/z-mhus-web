package de.mhus.cherry.web.api;

import java.io.File;

public interface VirtualWebSpace extends VirtualHost {

	File getConfigRoot();
	File getProjectRoot();
	File getDocumentRoot();
	
}
