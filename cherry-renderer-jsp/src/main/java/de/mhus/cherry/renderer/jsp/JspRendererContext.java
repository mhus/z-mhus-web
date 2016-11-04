package de.mhus.cherry.renderer.jsp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.Constants;
import org.apache.jasper.compiler.TldCache;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.ops4j.pax.web.jsp.JspServletWrapper;
import org.ops4j.pax.web.jsp.TldScanner;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.xml.sax.SAXException;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ProcessorContext;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.lang.DelegateClassLoader;
import de.mhus.osgi.sop.api.Sop;

public class JspRendererContext extends MLog implements ProcessorContext {

	private VirtualHost host;
	private ServletContext servletContext;
	private ServletConfig config;
	private HashMap<String, JspServletWrapper> wrappers = new HashMap<>();
	private JspServletWrapper servlet;
	private DelegateClassLoader hostClassLoader;
	private File root;
	private File tmp;
	
	public JspRendererContext(File root, File tmp) throws ServletException {
		this.root = root;
		this.tmp = tmp;
		init();
	}

	private void init() throws ServletException {
		servletContext = new JspDefaultServletContext( root, tmp );
		config = new DefaultServletConfig(servletContext);
		hostClassLoader = new DelegateClassLoader();
		// first of all my own classloader
		hostClassLoader.register(this.getClass().getClassLoader());
		// from classpath
		File classes = new File (root,"WEB-INF/classes");
		try {
			if (classes.exists() && classes.isDirectory()) {
				log().d("add classes to classpath",classes);
				hostClassLoader.register( new URLClassLoader(new URL[] {classes.toURL()}) );
			}
		} catch (Throwable t) {}
		// from libs
		{
			File lib = new File (root,"WEB-INF/lib");
			try {
				if (lib.exists() && lib.isDirectory()) {
					LinkedList<URL> libs = new LinkedList<>();
					for (File f : lib.listFiles())
						if (f.isFile() && f.getName().endsWith(".jar")) {
							log().d("add lib to classpath",f);
							libs.add(f.toURL());
						}
					if (libs.size() > 0)
						hostClassLoader.register( new URLClassLoader(libs.toArray(new URL[libs.size()]) ) );
				}
			} catch (Throwable t) {}
		}	
		// libs coming with renderer (default libs)
		{
			File renderRoot = Sop.getApi(CherryApi.class).getDeployDescritor(FrameworkUtil.getBundle(JspRenderer.class).getSymbolicName()).getPath(SPACE.PRIVATE);
			File lib = new File (renderRoot,"WEB-INF/lib");
			try {
				if (lib.exists() && lib.isDirectory()) {
					LinkedList<URL> libs = new LinkedList<>();
					for (File f : lib.listFiles())
						if (f.isFile() && f.getName().endsWith(".jar")) {
							log().d("add lib to classpath",f);
							libs.add(f.toURL());
						}
					if (libs.size() > 0)
						hostClassLoader.register( new URLClassLoader(libs.toArray(new URL[libs.size()]) ) );
				}
			} catch (Throwable t) {}
		}		
		
		// all bundles
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		for (Bundle b : bundle.getBundleContext().getBundles()) {
			if (b.getSymbolicName().equals(bundle.getSymbolicName())) continue;
			log().d("add bundle to classpath",b.getSymbolicName());
			ClassLoader bundleLoader = b.adapt(BundleWiring.class).getClassLoader();
//			hostClassLoader.add(new ClassLoaderResourceProvider(bundleLoader) );
			hostClassLoader.register(bundleLoader );
		}
		servlet = new JspServletWrapper(null,new MyJasperClassLoader( FrameworkUtil.getBundle(getClass()), hostClassLoader));
		servlet.init(config);
	}

	public boolean processRequest(CallContext context, File file) {

		//JspRequestWrapper req = new JspRequestWrapper(context, res, host, config);
		
//		if ( host.getHostClassLoader() != hostClassLoader)
//			try {
//				init();
//			} catch (ServletException e1) {
//				e1.printStackTrace();
//			}
		
		if (javax.servlet.jsp.JspFactory.getDefaultFactory() == null) {
			javax.servlet.jsp.JspFactory.setDefaultFactory( new org.apache.jasper.runtime.JspFactoryImpl() );
		}
		
		if (TldCache.getInstance(servletContext) == null) {
	        TldScanner scanner = new TldScanner(servletContext, true, false, false) {
	            @Override
				protected void scanPlatform() {
	            	try {
	            		URL url = JspRendererContext.class.getClassLoader().getResource("/META-INF/cherry-1.tld");
	            		TldResourcePath tldResourcePath = new TldResourcePath(url, null, null);
	            		parseTld(tldResourcePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        };
	        scanner.setClassLoader( hostClassLoader );
	        try {
				scanner.scan();
			} catch (IOException | SAXException e) {
				log().e(e);
			}
			servletContext.setAttribute(TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, new TldCache(servletContext, scanner.getUriTldResourcePathMap(),
                    scanner.getTldResourcePathTaglibXmlMap()));
		}
		
		HttpServletRequest req = context.getHttpRequest();
//		ExtendedServletResponse.inject(context);
//		ExtendedServletResponse resp = ExtendedServletResponse.getExtendedResponse(context);
		HttpServletResponse resp = context.getHttpResponse();
		
		try {
//			resp.setStatus(200);
//			resp.setContentType("text/plain");
			
			resp.setHeader("Pragma", "No-cache");
		    resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		    resp.setDateHeader("Expires", -1);
		    
			String fileRelativ = file.getAbsolutePath().substring( root.getAbsolutePath().length() );
			req.setAttribute(Constants.JSP_FILE, fileRelativ );
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
		return "jsp";
	}

	
}
