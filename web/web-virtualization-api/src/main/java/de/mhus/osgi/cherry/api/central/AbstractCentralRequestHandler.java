package de.mhus.osgi.cherry.api.central;

public abstract class AbstractCentralRequestHandler implements CentralRequestHandler, ConfigurableHandler {

	private boolean enabled = true;
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
