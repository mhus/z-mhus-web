package de.mhus.cherry.web.api;

import java.io.File;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.config.IConfig;

public interface WebSpace {

	File getConfigDirectory();
	File getProjectRoot();
	File getPageRoot();
	
	/**
	 * Get WebSpace configuration'
	 * 
	 * @return Config object
	 */
	IConfig getConfig();
	
	/**
	 * Web Space specific properties.
	 * @return Properties container
	 */
	IProperties getProperties();
	
}
