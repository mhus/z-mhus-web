package de.mhus.osgi.cherry.processor.jsp;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import de.mhus.osgi.cherry.impl.DefaultServletContext;
import de.mhus.osgi.cherry.impl.DefaultVirtualHost;

public class JspDefaultServletContext extends DefaultServletContext {
	
	// http://www.e-pde.gr/docs/jasper-howto.html
	public JspDefaultServletContext(DefaultVirtualHost host) {
		super(host);
		File tmp = new File( host.getTmpRoot(), "jsp");
		tmp.mkdirs();
		param.put("scratchdir", tmp.getAbsolutePath() );
		param.put("keepgenerated", "true");
		param.put("enablePooling", "false");
	}
	
	
}
