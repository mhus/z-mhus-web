package de.mhus.cherry.web.api;

import java.io.File;

public interface VirtualWebSpace extends VirtualHost {

	File getConfigDirectory();
	File getProjectRoot();
	File getPageRoot();
	
}
