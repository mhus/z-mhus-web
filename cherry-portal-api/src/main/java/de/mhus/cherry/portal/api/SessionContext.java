package de.mhus.cherry.portal.api;

public interface SessionContext {

	public ProcessorContext getProcessorContext(String name);
	
	public void setProcessorContext(String name, ProcessorContext context);

}
