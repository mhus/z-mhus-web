package de.mhus.osgi.cherry.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.naming.ldap.ExtendedRequest;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.w3c.dom.Element;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.directory.fs.FileResource;
import de.mhus.lib.core.directory.fs.FileResourceRoot;
import de.mhus.lib.core.logging.ConsoleFactory;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.cherry.api.MimeTypeFinder;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.central.CentralCallContext;
import de.mhus.osgi.cherry.api.util.AbstractVirtualHost;
import de.mhus.osgi.cherry.api.util.ExtendedServletResponse;

public class DefaultVirtualHost extends AbstractVirtualHost {

	public static final String DEFAULT_APPLICATION_ID = "default";
	
	private LinkedList<String> names = new LinkedList<>();
	private String applicationId;
	private ResourceNode applicationConfig;
	private VirtualApplication application;
	private File documentRoot;
	private File serverRoot;
	private File logRoot;
	private ConsoleFactory logFactory;
	private String name;
	private FileResourceRoot documentRootRes;
	private DefaultMimeTypeFinder mimeFinder;
	private File configRoot;
	private File binRoot;
	private WeakHashMap<String, ResourceNode> resourceCache = new WeakHashMap<>();
	private File tmpRoot;
	
	public DefaultVirtualHost(IConfig config) throws InstantiationException, MException, FileNotFoundException {
		
		for (ResourceNode name : config.getNodes("host")) {
			names.add( name.getExtracted("name"));
		}
		name = names.getFirst();
		
		ResourceNode app = config.getNode("application");
		if (app != null) {
			applicationId = app.getExtracted("id");
			applicationConfig = app.getNode("configuration");
		}
		if (applicationId == null) applicationId = DEFAULT_APPLICATION_ID;
		
		ResourceNode dir = config.getNode("directories");
		String serverRootStr = dir.getExtracted("serverRoot");
		if (serverRootStr == null) throw new InstantiationException("host root not found");
		serverRoot = new File(serverRootStr);
		
		documentRoot = new File(dir.getExtracted("docuemntRoot", serverRootStr + "/html"));
		logRoot = new File(dir.getExtracted("docuemntRoot", serverRootStr + "/log"));
		configRoot = new File(dir.getExtracted("documentRoot", serverRootStr + "/conf"));
		binRoot = new File(dir.getExtracted("bin", serverRootStr + "/bin"));
		tmpRoot = new File(dir.getExtracted("tmp", serverRootStr + "/tmp"));
		
		documentRoot.mkdirs();
		logRoot.mkdirs();
		configRoot.mkdirs();
		binRoot.mkdirs();
		tmpRoot.mkdirs();

		documentRootRes = new FileResourceRoot(documentRoot);
		
		logFactory = new ConsoleFactory(new PrintStream(new File(logRoot,"virtual.log")));
		log = logFactory.createInstance(name);
				
		mimeFinder = new DefaultMimeTypeFinder(this);
				
		URL[] urls = findBinaries("jar");
		classLoader = new URLClassLoader(urls,classLoader);
		
		doUpdateApplication();

	}

	@Override
	public URL[] findBinaries(String ext) {
		LinkedList<URL> list = new LinkedList<>();
		scanBinaries(list,binRoot, ext);
		return list.toArray(new URL[list.size()]);
	}

	private void scanBinaries(LinkedList<URL> list, File dir, String ext) {
		for (File file : dir.listFiles()) {
			if (file.isHidden() || file.getName().startsWith(".")) {
				
			} else
			if (file.isDirectory()) {
				scanBinaries(list, file, ext);
			} else
			if (file.isFile() && file.getName().endsWith("." + ext)) {
				try {
					list.add(file.toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getTagValue(Element root, String path, String def) {
		String out = def;
		Element tag = MXml.getElementByPath(root, path);
		if (tag != null) {
			out = MXml.getValue( tag, false );
			if (MString.isEmpty(out)) return def;
		}
		return out;
	}
	
	@Override
	public ResourceNode getResource(String target) {
		// need to ask application for resources. The application can redirect the resources to another source.
		// or use the getFileResource to load from document root
		if (application != null)
			return application.getResource(this, target);
		return null;
	}
	
	public ResourceNode getDocumentRootResource(String target) {
		ResourceNode res = resourceCache.get(target);
		if (res != null && ((FileResource)res).isValide())
			return res;
		res = documentRootRes.getResource(target);
		resourceCache.put(target, res);
		return res;
	}

	public File getDocumentRoot() {
		return documentRoot;
	}

	@Override
	public void processError(CentralCallContext context) {
		
		if (ExtendedServletResponse.isExtended(context)) {
			ExtendedServletResponse resp = ExtendedServletResponse.getExtendedResponse(context);
			int cs = resp.getStatus();
			if (cs != 0 && cs != 200) {
				if (application != null)
					application.processError(this, context, cs);
			}
		}
		
	}

	public List<String> getHostNames() {
		return names;
	}

	@Override
	public boolean processRequest(CentralCallContext context) throws Exception {
		
		if (application != null)
			if (application.processRequest(this, context)) return true;
		
//		ResourceNode res = getResource(context.getTarget());
//		return deliverStaticContent(context, res, true);
		return false;
	}


	public File getServerRoot() {
		return serverRoot;
	}

	public File getConfigRoot() {
		return configRoot;
	}

	public void doUpdateApplication() {
		if (applicationId == null) return;
		
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		try {
			for (ServiceReference<VirtualApplication> ref : context.getServiceReferences(VirtualApplication.class, "(name="+applicationId+")") ) {
				VirtualApplication service = context.getService(ref);
				doUpdateApplication(context, ref, service);
				return;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		application = null;
	}

	public void doUpdateApplication(BundleContext cb,
			ServiceReference<VirtualApplication> reference,
			VirtualApplication service) {
		
		if (reference.getProperty("name") != null && reference.getProperty("name").equals(applicationId)) {
			application = service;
			if (application != null) {
				try {
					application.configureHost(this,applicationConfig);
					
					URL[] urls = findBinaries("jar");
					classLoader = new URLClassLoader(urls, application.getApplicationClassLoader() );
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				URL[] urls = findBinaries("jar");
				classLoader = new URLClassLoader(urls, getClass().getClassLoader() );
			}
		}
		
	}
	
	public MimeTypeFinder getMimeTypeFinder() {
		return mimeFinder;
	}

	public File getTmpRoot() {
		return tmpRoot;
	}
	
}
