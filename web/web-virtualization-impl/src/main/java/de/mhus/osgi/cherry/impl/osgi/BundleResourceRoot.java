package de.mhus.osgi.cherry.impl.osgi;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.directory.ResourceNode;

public class BundleResourceRoot extends BundleResource {

	protected Bundle bundle;

	public BundleResourceRoot(Bundle bundle, String path) {
		super(null, path, null);
		this.bundle = bundle;
		this.root = this;
	}
	
	public Bundle getBundle() {
		return bundle;
	}

	public ResourceNode getResource(String target) {
		
		return getResource(this,target);
	}

	private ResourceNode getResource(BundleResource parent,
			String target) {
		if (parent == null || target == null) return null;
		if (target.length() == 0) return parent;
		
		String next = null;
		if (MString.isIndex(target, '/')) {
			next = MString.beforeIndex(target, '/');
			target = MString.afterIndex(target, '/');
		} else {
			next = target;
			target = "";
		}
		if (next.length() == 0) return getResource(parent,target);
		
		ResourceNode n = parent.getNode(next + ( target.length() == 0 ? "" : "/") );
		if (n == null) return null;
		
		return getResource((BundleResource) n, target);
	}

}
