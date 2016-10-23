package de.mhus.cherry.portal.impl;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import de.mhus.cherry.portal.api.ProcessorContext;
import de.mhus.cherry.portal.api.SessionContext;

public class DefaultSessionContext implements SessionContext {

	protected HashMap<String, ProcessorContext> processorContexts = new HashMap<>();
	private CherryServlet servlet;
	private HttpSession session;

	public DefaultSessionContext(CherryServlet servlet, HttpSession session) {
		this.servlet = servlet;
		this.session = session;
	}

	@Override
	public ProcessorContext getProcessorContext(String name) {
		return processorContexts.get(name);
	}

	@Override
	public void setProcessorContext(String name, ProcessorContext context) {
		processorContexts.put(context.getName(), context);
	}

}
