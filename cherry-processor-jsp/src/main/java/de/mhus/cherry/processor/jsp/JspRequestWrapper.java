package de.mhus.cherry.processor.jsp;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequestWrapper;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.impl.DefaultServletConfig;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

public class JspRequestWrapper extends HttpServletRequestWrapper {

	private CentralCallContext context;
	private DefaultVirtualHost host;
	private ResourceNode res;
	private DefaultServletConfig config;

	public JspRequestWrapper(CentralCallContext context, ResourceNode res,
			DefaultVirtualHost host, DefaultServletConfig config) {
		super(context.getRequest());
		this.context = context;
		this.host = host;
		this.res = res;
		this.config = config;
	}

	@Override
	public String getContextPath() {
		return super.getContextPath();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return super.getRequestDispatcher(path);
	}

	
}
