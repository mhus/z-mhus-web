package de.mhus.osgi.cherry.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.directory.fs.FileResource;
import de.mhus.lib.errors.MException;
import de.mhus.lib.portlet.resource.Resource;
import de.mhus.osgi.cherry.api.ApplicationContext;
import de.mhus.osgi.cherry.api.ProcessorMatcher;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.central.CentralCallContext;

public class DefaultApplicationContext implements ApplicationContext {

	protected LinkedList<String> indexes = new LinkedList<>();
	{
		indexes.add("index.html");
		indexes.add("index.jsp");
		indexes.add("index.php");
	}
	protected HashMap<Integer, String> errorPages = new HashMap<>();
	protected HashMap<String, Resource> resources = new HashMap<>();
	protected HashMap<String, ProcessorMatcher> processorMapping = null;
	private VirtualHost host;

	public void doActivate(VirtualApplication defaultApplication,
			VirtualHost host, ResourceNode config) throws Exception {

		this.host = host;
		
		if (config != null) {
			ResourceNode index = config.getNode("indexes");
			if (index != null) {
				indexes.clear();
				for (ResourceNode i : index.getNodes("index")) {
					indexes.add(i.getExtracted("name"));
				}
			}
			
			ResourceNode error = config.getNode("errors");
			if (error != null) {
				for (ResourceNode page : error.getNodes("page")) {
					errorPages.put(page.getInt("code", -1), page.getExtracted("name"));
				}
			}
			
			ResourceNode resourceHandlers = config.getNode("resources");
			if (resourceHandlers != null) {
				for (ResourceNode rh : resourceHandlers.getNodes("handler")) {
					String path = rh.getExtracted("path");
					String clazzName = rh.getExtracted("class");
					if (path != null && clazzName != null) {
						try {
							Class<?> clazz = host.getHostClassLoader().loadClass(clazzName);
							Resource obj = (Resource) clazz.newInstance();
							resources.put(path, obj);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
			}
			
		}
		
	}


	public boolean deliverContent(VirtualHost host, CentralCallContext context, ResourceNode res) throws Exception {
		return deliverStaticContent(host, context, res, true);
	}
	
	public boolean deliverStaticContent(VirtualHost host, CentralCallContext context, ResourceNode res, boolean setResponseOk) throws Exception {
		
		if (res == null) {
//			context.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		
		InputStream is = res.getInputStream();
		if (is == null) {
			return false;
		}
		
		long len = res.getLong(FileResource.KEYS.LENGTH.name(), -1);
		if (len >= 0 && len < Integer.MAX_VALUE)
			context.getResponse().setContentLength((int)len);
		context.getResponse().setContentType( host.getMimeTypeFinder().getMimeType( res ) ); //TODO find mime
		if (setResponseOk) context.getResponse().setStatus(HttpServletResponse.SC_OK);
		ServletOutputStream os = context.getResponse().getOutputStream();
		MFile.copyFile(is, os);
		os.flush();
		
		return true; // consumed
	}

	public void processError(CentralCallContext context, int cs) {
		String errorPagePath = errorPages.get(cs);
		if (errorPagePath == null) 
			errorPagePath = errorPages.get(0);
		if (errorPagePath != null) {
			ResourceNode res = host.getResource(errorPagePath);
			try {
				deliverStaticContent(host, context, res, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Resource getResourceHandler(CentralCallContext context) {
		return resources.get(context.getTarget());
	}
	
	public HashMap<String, ProcessorMatcher> getProcessorMapping() {
		return processorMapping;
	}


	@Override
	public ResourceNode findIndex(CentralCallContext context, ResourceNode res) {
		String target = context.getTarget();
		if (!target.endsWith("/")) target = target + "/";
		for (String in : indexes) {
			// do not use res.getNode(in), another resource could be mounted - TODO need to use a cascaded resource
			ResourceNode inn = host.getResource(target + in);
			if (inn != null && inn.hasContent()) {
				return inn;
			}
		}
		return null;
	}

	public VirtualHost getHost() {
		return host;
	}
	
}
