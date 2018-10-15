package de.mhus.cherry.web.util.webspace;

import java.io.File;
import java.util.Date;
import java.util.Map.Entry;

import de.mhus.cherry.web.api.WebArea;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.cherry.web.api.VirtualWebSpace;
import de.mhus.cherry.web.util.AbstractVirtualHost;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.core.io.FileWatch;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.services.util.OsgiBundleClassLoader;

public abstract class AbstractWebSpace extends AbstractVirtualHost implements VirtualWebSpace {

	protected File root;
	private IConfig cServer;
	private File configRoot;
	private File documentRoot;
	private FileWatch configWatch;
	private Date updated;
	
	public void setRoot(String rootPath) throws MException {
		root = new File(rootPath);
	}
	
	@Override
	public void start(CherryApi api) throws MException {
		updated = new Date();
		if (!root.exists())
			throw new MException("root for webspace not exists",root);
		if (!root.isDirectory())
			throw new MException("root for webspace not a directory",root);
		
		configRoot = new File(root, "conf"); // default configuration directory
		if (!configRoot.exists() || !configRoot.isDirectory())
			configRoot = root; // fall back to root directory
		
		String configFile = prepareConfigName("server");
		config = MConfig.find(configRoot, configFile, true);
		if (config == null)
			throw new MException("config for webspace not found",root);
		// get server config
		cServer = config.getNode("server");
		if (cServer == null)
			throw new MException("server in config not found",root);
		// get alias
		setConfigAliases(MConfig.toStringArray(cServer.getNode("aliases").getNodes(), "value"));
		// set name
		name = getFirstAlias(); // default
		name = cServer.getString("name", name);
		// get config root
		if (cServer.isProperty("configurationRoot"))
			configRoot = findProjectFile(cServer.getString("configurationRoot"));
		// get default header entries
		IConfig cHeaders = cServer.getNode("headers");
		if (cHeaders != null) {
			for (Entry<String, Object> entry : cHeaders.entrySet())
				headers.put(entry.getKey(), String.valueOf(entry.getValue()));
		}
		// document root
		documentRoot = findProjectFile(cServer.getString("documentRoot", "html"));
		// trace options
		traceErrors = cServer.getBoolean("traceErrors", true);
		traceAccess = cServer.getBoolean("traceAccess", false);
		// defaultMimeType
		defaultMimeType = cServer.getString("defaultMimeType", defaultMimeType);
		// load filters
		OsgiBundleClassLoader loader = new OsgiBundleClassLoader();
		if (cServer.getNode("filters") != null) {
			for (IConfig filterDef : cServer.getNode("filters").getNodes()) {
				String filterClazzName = filterDef.getString("class");
				try {
					Class<?> clazz = loader.loadClass(filterClazzName);
					WebFilter filter = (WebFilter) clazz.newInstance();
					filter.doInitialize(getInstanceId(), this, filterDef);
					addFilter(filter);
				} catch (ClassNotFoundException e) {
					throw new MException("filter not found",filterClazzName);
				} catch (Throwable e) {
					throw new MException("can't instanciate filter",filterClazzName,e);
				}
			}
		}
		// load active areas
		if (cServer.getNode("areas") != null) {
			for (IConfig areaDef : cServer.getNode("areas").getNodes()) {
				String areaPath = areaDef.getString("path");
				String areaClazzName = areaDef.getString("class");
				try {
					Class<?> clazz = loader.loadClass(areaClazzName);
					WebArea area = (WebArea) clazz.newInstance();
					area.doInitialize(getInstanceId(), this, areaDef);
					addArea(areaPath, area);
				} catch (ClassNotFoundException e) {
					throw new MException("area not found",areaClazzName);
				} catch (Throwable e) {
					throw new MException("can't instanciate area",areaClazzName,e);
				}			
			}
		}
		
		if (cServer.getBoolean("watchConfiguration", true)) {
			configWatch = new FileWatch(new File(configFile + ".json"), new FileWatch.Listener() { // TODO could also be another extension
				
				@Override
				public void onFileWatchError(FileWatch fileWatch, Throwable t) {
				}
				
				@Override
				public void onFileChanged(FileWatch fileWatch) {
					log().i("configuration changed: restart",getName());
					api.restart(AbstractWebSpace.this);
				}
			}).doStart();
		}
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
		if (configWatch != null)
			configWatch.cancel();
		configWatch = null;
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
	
	@Override
	public Date getUpdated() {
		return updated;
	}

}
