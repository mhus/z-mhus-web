package de.mhus.osgi.cherry.processor.php;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.directory.fs.FileResource;
import de.mhus.osgi.cherry.api.ProcessorMatcher;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualFileProcessor;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.impl.DefaultServletConfig;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

@Component(name="PhpProcessor",immediate=true,properties="name=" + PhpApplication.NAME)
public class PhpApplication implements VirtualFileProcessor {

	public static final String NAME = "php";

	private DefaultVirtualHost host;
	private ServletContext servletContext;
	private DefaultServletConfig config;

	@Override
	public boolean processRequest(VirtualHost host, ResourceNode res, CentralCallContext context) throws Exception {
		
//		InputStream is = res.getInputStream();
//		if (is == null) return false;
//		is.close();
	
		if ( res.getProperty(FileResource.KEYS.TYPE.name()) != FileResource.TYPE.FILE) return false;
		
		PhpContext ctx = (PhpContext) host.getProcessorContext(NAME);
		if (ctx == null) {
			try {
				ctx = new PhpContext(host);
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
				addFilter(".*\\.php");
				setProcessor(NAME);
			}
		};
	}

}
