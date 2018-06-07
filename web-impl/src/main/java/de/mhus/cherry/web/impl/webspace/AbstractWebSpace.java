package de.mhus.cherry.web.impl.webspace;

import java.io.File;

import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.api.VirtualWebSpace;
import de.mhus.cherry.web.impl.AbstractVirtualHost;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.errors.MException;

public abstract class AbstractWebSpace extends AbstractVirtualHost implements VirtualWebSpace {

	protected File root;
	private IConfig cServer;
	private File configRoot;
	private File documentRoot;
	
	public void setRoot(String rootPath) throws MException {
		root = new File(rootPath);
	}
	
	@Override
	public void start(CherryApi api) throws MException {
		if (!root.exists())
			throw new MException("root for webspace not exists",root);
		if (!root.isDirectory())
			throw new MException("root for webspace not a directory",root);
		
		configRoot = new File(root, "conf"); // default configuration directory
		if (!configRoot.exists() || !configRoot.isDirectory())
			configRoot = root; // fall back to root directory
		
		config = MConfig.find(configRoot, "server", true);
		if (config == null)
			throw new MException("config for webspace not found",root);
		cServer = config.getNode("server");
		if (cServer == null)
			throw new MException("server in config not found",root);
		alias = cServer.getString("alias");
		if (cServer.isProperty("configurationRoot"))
			configRoot = findProjectFile(cServer.getString("configurationRoot"));
		documentRoot = findProjectFile(cServer.getString("documentRoot", "html"));
		traceErrors = cServer.getBoolean("traceErrors", false);
		traceAccess = cServer.getBoolean("traceAccess", false);
	}
	
	public File findProjectFile(String path) {
		if (path.startsWith("/")) {
			if (MSystem.isWindows())
				return new File(path.substring(1));
			else
				return new File(path);
		}
		return new File(root, path);
	}

	@Override
	public void stop(CherryApi api) {
		root = null;
		config = null;
		cServer = null;
		alias = null;
	}

	@Override
	public File getConfigRoot() {
		return configRoot;
	}

	@Override
	public File getProjectRoot() {
		return root;
	}

	@Override
	public File getDocumentRoot() {
		return documentRoot;
	}

}
