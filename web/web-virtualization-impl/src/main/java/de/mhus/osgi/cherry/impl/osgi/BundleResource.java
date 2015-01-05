package de.mhus.osgi.cherry.impl.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;

public class BundleResource extends ResourceNode {

	public enum KEYS {NAME, MODIFIED, TYPE};
	public enum TYPE {FILE,DIRCTORY}

	protected BundleResourceRoot root;
	private String path;
	private HashMap<String,BundleResource> cache = new HashMap<>();
	private URL url;
	private boolean dir;
	private String name;
	private BundleResource parent;

	public BundleResource(BundleResourceRoot root, String path, BundleResource parent) {
		this.root = root;
		this.path = path;
		this.dir = path.endsWith("/");
		this.parent = parent;
		if (!dir)
			this.url = getBundle().getEntry(path);
		
		if (dir)
			name = MString.afterLastIndex(path.substring(0, path.length()-1), '/');
		else
			name = MString.afterLastIndex(path, '/');
		
	}
	
	public Bundle getBundle() {
		return root.getBundle();
	}

	@Override
	public String[] getPropertyKeys() {
		KEYS[] v = KEYS.values();
		String[] out = new String[v.length];
		for (int i = 0; i < v.length; i++)
			out[i] = v[i].name().toLowerCase();
		return out;
	}

	@Override
	public ResourceNode getNode(String key) {
		if (!dir) return null;
		if (key == null) return null;
		boolean keyDir = false;
		if (key.endsWith("/")) {
			keyDir = true;
			key = key.substring(0,  key.length() -1);
		}
		if (key.equals("") || key.equals("..") || key.equals(".")) return null;
		if (key.indexOf('/') > -1 || key.indexOf('\\') > -1) return null; // only direct children
		// TODO special chars ?!!

		if (keyDir) key = key + "/";
		
		BundleResource cached = cache.get(key);
		if (cached != null) {
			if (cached.isValide()) return cached;
			cache.remove(key);
		}

		BundleResource sub = new BundleResource(root, path + key, this );
		if (!sub.isValide()) return null;
		
		cache.put(key, sub);
		return sub;
	}

	public boolean isValide() {
		return dir || url != null; // resource should be static
	}

	@Override
	public ResourceNode[] getNodes() {
		Enumeration<String> en = getBundle().getEntryPaths(path);
		LinkedList<ResourceNode> out = new LinkedList<>();
		while (en.hasMoreElements()) {
			String entry = en.nextElement();
			entry = entry.substring(path.length());
			ResourceNode sub = getNode(entry);
			if (sub != null) {
				out.add(sub);
			}
		}
		return out.toArray(new ResourceNode[out.size()]);
	}

	@Override
	public ResourceNode[] getNodes(String key) {
		ResourceNode n = getNode(key);
		if (n == null) return new ResourceNode[0];
		return new ResourceNode[] {n};
	}

	@Override
	public String[] getNodeKeys() {
		LinkedList<String> out = new LinkedList<>();
		for (ResourceNode sub : getNodes()) {
			try {
				out.add(sub.getName());
			} catch (MException e) {
			}
		}
		return out.toArray(new String[out.size()]);
	}

	@Override
	public String getName() throws MException {
		return name;
	}

	@Override
	public InputStream getInputStream(String key) {
		if (dir) return null;
		try {
			return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResourceNode getParent() {
		return parent;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public Object getProperty(String name) {
		try {
			KEYS key = KEYS.valueOf(name.toUpperCase());
			switch (key) {
			case NAME:
				return name;
			case TYPE:
				if (dir)
					return TYPE.DIRCTORY;
				else
					return TYPE.FILE;
			case MODIFIED:
				return getBundle().getLastModified();
			}
		} catch (IllegalArgumentException e) {}
		return null;
	}

	@Override
	public boolean isProperty(String name) {
		try {
			KEYS key = KEYS.valueOf(name.toUpperCase());
			return key != null;
		} catch (Throwable t) {
		}
		return false;
	}

	@Override
	public void removeProperty(String key) {
	}

	@Override
	public void setProperty(String key, Object value) {
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean hasContent() {
		return !dir;
	}

}
