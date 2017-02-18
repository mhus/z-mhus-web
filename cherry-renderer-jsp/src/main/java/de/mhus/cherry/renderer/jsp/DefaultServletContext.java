package de.mhus.cherry.renderer.jsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.util.AbstractServletContext;
import de.mhus.osgi.sop.api.Sop;

@SuppressWarnings("unchecked")
public class DefaultServletContext extends AbstractServletContext {

	private File root;

	public DefaultServletContext(File root) {
		this.root = root;
	}
	
	@Override
	public String getContextPath() {
		return "/";
	}

	@Override
	public String getMimeType(String file) {
		return Sop.getApi(CherryApi.class).getMimeType(file);
	}

	@Override
	public Set getResourcePaths(String path) {
		File file = new File(root,path);
		if (!file.exists()) return null;
		if (!path.endsWith("/")) path = path + "/";
		HashSet<String> out = new HashSet<>();
		for (File sub : file.listFiles()) {
			try {
				if (sub.isDirectory()) {
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
		//if (path.startsWith("/")) return new File(path).toURL();
		File file = new File(root,path);
		if (!file.exists()) return null;
		return file.toURL();
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		File file = new File(root,path);
		if (!file.exists()) return null;
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	@Override
	public String getRealPath(String path) {
		File file = new File(root,path);
		if (!file.exists()) return null;
		return file.getAbsolutePath();
	}

}
