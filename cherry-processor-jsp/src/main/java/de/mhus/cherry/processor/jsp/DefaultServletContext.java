package de.mhus.cherry.processor.jsp;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.util.AbstractServletContext;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.directory.fs.FileResource;

public class DefaultServletContext extends AbstractServletContext {

	private VirtualHost host;

	public DefaultServletContext(VirtualHost host) {
		this.host = host;
	}
	
	@Override
	public String getContextPath() {
		return "/";
	}

	@Override
	public String getMimeType(String file) {
		return host.getMimeTypeFinder().getMimeType(file);
	}

	@Override
	public Set getResourcePaths(String path) {
		ResourceNode res = host.getResource(path);
		if (res == null) return null;
		if (!path.endsWith("/")) path = path + "/";
		HashSet<String> out = new HashSet<>();
		for (ResourceNode sub : res.getNodes()) {
			try {
				if (sub.getProperty(FileResource.KEYS.TYPE.name()) == FileResource.TYPE.DIRCTORY) {
					out.add(path + sub.getName() + "/");
				} else {
					out.add(path + sub.getName());
				}
			} catch (Throwable t) {}
		}
		return out;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		ResourceNode res = host.getResource(path);
		if (res == null) return null;
		return res.getUrl();
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		ResourceNode res = host.getResource(path);
		if (res == null) return null;
		return res.getInputStream();
	}

	@Override
	public String getRealPath(String path) {
		ResourceNode res = host.getResource(path);
		if (res == null) return null;
		String file = (String) res.getProperty("filepath");
		return file;
	}

}
