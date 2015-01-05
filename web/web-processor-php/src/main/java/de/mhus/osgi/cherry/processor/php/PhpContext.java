package de.mhus.osgi.cherry.processor.php;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.caucho.quercus.servlet.QuercusServlet;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.cherry.api.ProcessorContext;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.api.util.ExtendedServletResponse;
import de.mhus.osgi.cherry.impl.DefaultServletConfig;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

public class PhpContext implements ProcessorContext {

	private DefaultVirtualHost host;
	private ServletContext servletContext;
	private DefaultServletConfig config;
	private QuercusServlet servlet;

	public PhpContext(VirtualHost host) throws ServletException {
		this.host = (DefaultVirtualHost) host;
		servletContext = new PhpDefaultServletContext(this.host);
		config = new DefaultServletConfig(servletContext);
		servlet = new QuercusServlet();
		servlet.init(config);
	}

	public boolean processRequest(CentralCallContext context, ResourceNode res) {
		
		HttpServletRequest req = context.getRequest();
		ExtendedServletResponse.inject(context);
		ExtendedServletResponse resp = ExtendedServletResponse.getExtendedResponse(context);
		try {
			resp.setStatus(200);
			resp.setContentType("text/plain");
			servlet.service(req, resp);
			resp.flushBuffer();
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public String getName() {
		return PhpApplication.NAME;
	}

}
