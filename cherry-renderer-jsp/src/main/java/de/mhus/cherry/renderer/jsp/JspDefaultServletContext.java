package de.mhus.cherry.renderer.jsp;

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

import de.mhus.cherry.portal.api.VirtualHost;

public class JspDefaultServletContext extends DefaultServletContext {
	
	// http://www.e-pde.gr/docs/jasper-howto.html
	public JspDefaultServletContext(File root ) {
		super(root);
//		File tmp = new File( host.getTmpRoot(), "jsp");
//		tmp.mkdirs();
//		param.put("scratchdir", tmp.getAbsolutePath() );
		param.put("keepgenerated", "true");
		param.put("enablePooling", "false");
	}
	
	
}
