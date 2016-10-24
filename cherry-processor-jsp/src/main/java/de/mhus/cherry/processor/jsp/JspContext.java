package de.mhus.cherry.processor.jsp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.Constants;
import org.apache.jasper.servlet.JspServlet;
import org.ops4j.pax.web.jsp.JasperClassLoader;
import org.ops4j.pax.web.jsp.JspServletWrapper;
import org.osgi.framework.FrameworkUtil;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.core.directory.ResourceNode;

public class JspContext implements ProcessorContext {

	private VirtualHost host;
	private ServletContext servletContext;
	private ServletConfig config;
	private HashMap<String, JspServletWrapper> wrappers = new HashMap<>();
	private JspServletWrapper servlet;
	private ClassLoader hostClassLoader;
	
	public JspContext(VirtualHost host) throws ServletException {
		this.host = host;
		init();
	}

	private void init() throws ServletException {
		servletContext = new JspDefaultServletContext(this.host);
		config = new DefaultServletConfig(servletContext);
		hostClassLoader = getClass().getClassLoader();
		servlet = new JspServletWrapper(null,new MyJasperClassLoader( FrameworkUtil.getBundle(getClass()), hostClassLoader));
		servlet.init(config);
	}

	public boolean processRequest(CallContext context, ResourceNode res) {

		//JspRequestWrapper req = new JspRequestWrapper(context, res, host, config);
		
//		if ( host.getHostClassLoader() != hostClassLoader)
//			try {
//				init();
//			} catch (ServletException e1) {
//				e1.printStackTrace();
//			}
		
		HttpServletRequest req = context.getHttpRequest();
		ExtendedServletResponse.inject(context);
		ExtendedServletResponse resp = ExtendedServletResponse.getExtendedResponse(context);
		try {
			resp.setStatus(200);
			resp.setContentType("text/plain");
			req.setAttribute(Constants.JSP_FILE, );
			servlet.service(req, resp);
			resp.flushBuffer();
		} catch (ServletException | IOException e) {
//		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public String getName() {
		return JspApplication.NAME;
	}
	
}
