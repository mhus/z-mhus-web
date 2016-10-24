package de.mhus.cherry.processor.jsp;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class DefaultServletConfig implements ServletConfig {

	private ServletContext context;

	public DefaultServletConfig(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public String getServletName() {
		return context.getServletContextName();
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public String getInitParameter(String name) {
		return context.getInitParameter(name);
	}

	@Override
	public Enumeration getInitParameterNames() {
		return context.getInitParameterNames();
	}

}
