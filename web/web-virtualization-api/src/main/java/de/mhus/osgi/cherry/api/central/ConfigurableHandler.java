package de.mhus.osgi.cherry.api.central;

import java.util.Properties;

public interface ConfigurableHandler {

	void configure(Properties rules);
	
	void setEnabled(boolean enabled);
}
