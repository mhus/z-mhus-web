package de.mhus.osgi.cherry.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.config.XmlConfig;
import de.mhus.lib.core.logging.Log;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.cherry.api.VirtualHost;
import de.mhus.osgi.cherry.api.VirtualHostProvider;

@Component(immediate=true)
public class DefaultHostProvider extends MLog implements VirtualHostProvider {

	
	private File configDir = new File("etc/vhosts");
	private HashMap<String,VirtualHost> hostMapping = new HashMap<>();
	private HashMap<String,VirtualHost> hostMappingCache = new HashMap<>();
	private ServiceTracker<VirtualApplication, VirtualApplication> applicationTracker;
	private BundleContext cb;
	
	@Activate
	public void doActivate(ComponentContext ctx) {
		updateConfiguration();
		cb = ctx.getBundleContext();
		applicationTracker = new ServiceTracker<>(cb, VirtualApplication.class, new MyCustomizer());
		applicationTracker.open();
	}
	

	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		hostMapping.clear();
		hostMappingCache.clear();
		applicationTracker.close();
	}
	
	private void updateConfiguration() {
		synchronized (hostMapping) {
			hostMapping.clear();
			hostMappingCache.clear();
			loadDirectory(configDir);
		}
	}
	
	private void loadDirectory(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				try {
					java.io.FileInputStream is = new FileInputStream(file);
					Document doc = MXml.loadXml(is);
					loadDocument(doc);
					is.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else
			if (file.isDirectory())
				loadDirectory(file);
		}
	}


	private void loadDocument(Document doc) {
		for (Element host : MXml.getLocalElementIterator(doc.getDocumentElement(), "virtualhost")) {
			loadVHost(host);
		}
	}


	private void loadVHost(Element host) {
		try {
			XmlConfig config = new XmlConfig(host);
			DefaultVirtualHost vhost = new DefaultVirtualHost(config);
			for (String name : vhost.getHostNames()) {
				hostMapping.put(name, vhost);
				hostMappingCache.clear();
			}
		} catch (Throwable t) {
			t.printStackTrace(); //TODO LOG!
		}
	}

	@Override
	public String[] getProvidedHosts() {
		synchronized (hostMapping) {
			return hostMapping.keySet().toArray(new String[hostMapping.size()]);
		}
	}

	@Override
	public boolean existsHost(String host) {
		return findHost(host) != null;
	}

	protected VirtualHost findHost(String host) {
		synchronized (hostMapping) {
			VirtualHost out = hostMappingCache.get(host);
			if (out == null) {
				for (Map.Entry<String, VirtualHost> entry : hostMapping.entrySet()) {
					if (host.matches(entry.getKey())) {
						out = entry.getValue();
						hostMappingCache.put(host, out);
						log().d("found host",host,out,entry.getKey());
						break;
					}
				}
			}
			if (out == null)
				log().d("vhost not found",host);
			
			return out;
		}
	}


	@Override
	public VirtualHost getHost(String host) {
		return findHost(host);
	}

	private class MyCustomizer implements ServiceTrackerCustomizer<VirtualApplication, VirtualApplication> {

		@Override
		public VirtualApplication addingService(
				ServiceReference<VirtualApplication> reference) {
			
			VirtualApplication service = cb.getService(reference);
			
			synchronized (hostMapping) {
				HashSet<VirtualHost> vh = new HashSet<>();
				vh.addAll(hostMapping.values());
				hostMappingCache.clear();
				for (VirtualHost host : vh)
					((DefaultVirtualHost)host).doUpdateApplication(cb,reference,service);
			}

			return service;
		}

		@Override
		public void modifiedService(
				ServiceReference<VirtualApplication> reference,
				VirtualApplication service) {
			synchronized (hostMapping) {
				HashSet<VirtualHost> vh = new HashSet<>();
				vh.addAll(hostMapping.values());
				hostMappingCache.clear();
				for (VirtualHost host : vh)
					((DefaultVirtualHost)host).doUpdateApplication(cb,reference,service);
			}
			
		}

		@Override
		public void removedService(
				ServiceReference<VirtualApplication> reference,
				VirtualApplication service) {
			
			synchronized (hostMapping) {
				HashSet<VirtualHost> vh = new HashSet<>();
				vh.addAll(hostMapping.values());
				hostMappingCache.clear();
				for (VirtualHost host : vh)
					((DefaultVirtualHost)host).doUpdateApplication(cb,reference,null);
			}
			
		}
		
	}
}
