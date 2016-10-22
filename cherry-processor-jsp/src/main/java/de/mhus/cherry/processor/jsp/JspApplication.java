package de.mhus.cherry.processor.jsp;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.ProcessorMatcher;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualFileProcessor;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.impl.DefaultServletConfig;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

@Component(name="JspProcessor",immediate=true,properties="name=" + JspApplication.NAME)
public class JspApplication implements VirtualFileProcessor {

	public static final String NAME = "jsp";
	
	private DefaultVirtualHost host;
	private ServletContext servletContext;
	private DefaultServletConfig config;

	@Override
	public boolean processRequest(VirtualHost host, ResourceNode res, CentralCallContext context) throws Exception {

		if (res == null) return false;
		
//		InputStream is = res.getInputStream();
//		if (is == null) return false;
//		is.close();
			
		JspContext ctx = (JspContext) host.getProcessorContext(NAME);
		if (ctx == null) {
			try {
				ctx = new JspContext(host);
				host.setProcessorContext(ctx);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		if (ctx == null) {
			context.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return true;
		}
		
		return ctx.processRequest(context,res);
	}

	@Override
	public ProcessorMatcher getDefaultMatcher() {
		return new ProcessorMatcher() {
			{
				addFilter(".*\\.jsp");
				setProcessor(NAME);
			}
		};
	}

}
