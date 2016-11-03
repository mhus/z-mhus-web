package de.mhus.cherry.portal.impl.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.cfg.CfgFile;
import de.mhus.lib.core.logging.Log;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = Servlet.class, properties = { "alias=/.pub" }, name="CherryDeployServlet",servicefactory=true)
public class CherryDeployServlet extends HttpServlet implements BundleListener {

	public enum SENSIVITY {CHECK,WRITE,UPDATE,OVERWRITE,CLEANUP,RESET};
	
	private static Log log = Log.getLog(CherryDeployServlet.class);
	private static final long serialVersionUID = 1L;
	private static final CfgFile publicStore = new CfgFile(CherryDeployServlet.class,"publicStore", new File("cherry/public")) {
		@Override
		protected void onPostUpdate(File newValue) {
			newValue.mkdirs();
		}
	};
	private static final CfgFile privateStore = new CfgFile(CherryDeployServlet.class,"privateStore", new File("cherry/private")) {
		@Override
		protected void onPostUpdate(File newValue) {
			newValue.mkdirs();
		}
	};
	private static final CfgFile tempStore = new CfgFile(CherryDeployServlet.class,"tempStore", new File("cherry/temp")) {
		@Override
		protected void onPostUpdate(File newValue) {
			newValue.mkdirs();
		}
	};
	private BundleContext context;
	public static CherryDeployServlet instance;
	private HashMap<String, MyDeployDescriptor> descriptors = new HashMap<>();
	
	
    @Activate
    public void activate(ComponentContext ctx) {
		context = ctx.getBundleContext();
		context.addBundleListener(this);
		instance = this;
		refreshAll(SENSIVITY.CHECK);
    }
    
    @Deactivate
    public void deactivate(ComponentContext ctx) {
    	context.removeBundleListener(this);
    	instance = null;
    }
	
	@Override
	public void bundleChanged(BundleEvent event) {

		if (event.getType() == BundleEvent.STARTED || event.getType() == BundleEvent.UPDATED) {
			doBundle(event.getBundle(), SENSIVITY.UPDATE);
		} else
		if (event.getType() == BundleEvent.STOPPING ) {
			doDelete(event.getBundle());
		}
		
	}

	public void doDelete(Bundle bundle) {
		Enumeration<String> list = bundle.getEntryPaths("/CHERRY");
		if (list == null) return;
		log.i("Delete Bundle: " + bundle.getSymbolicName());
		while (list.hasMoreElements()) {
			String path = list.nextElement();
			if (path.equals("CHERRY/public/")) {
				doDelete(bundle,bundle.getEntry(path), publicStore.value(), SPACE.PUBLIC);
			} else
			if (path.equals("CHERRY/private/")) {
				doDelete(bundle,bundle.getEntry(path), privateStore.value(), SPACE.PRIVATE);
			}
		}
	}

	private void doDelete(Bundle bundle, URL entry, File file, SPACE space) {
		log.i("Delete",file, bundle.getSymbolicName());
		
		String path = entry.getPath();
		
		MProperties config = null;
		String configPath = path + "content.properties";
		URL configEntry = bundle.getEntry(configPath);
		if (configEntry != null) {
			try {
				InputStream is = configEntry.openStream();
				config = MProperties.load(is);
				is.close();
			} catch (Throwable t) {}
		}
		if (config == null) config = new MProperties();
		
		file = new File(file, config.getString("name", bundle.getSymbolicName() ));
		
		MFile.deleteDir(file);
		
	}
	
	private void doBundle(Bundle bundle, SENSIVITY sensivity) {
		Enumeration<String> list = bundle.getEntryPaths("/CHERRY");
		if (list == null) return;
		log.i("Export Bundle: " + bundle.getSymbolicName());
		while (list.hasMoreElements()) {
			String path = list.nextElement();
			if (path.equals("CHERRY/public/")) {
				doExport(bundle,bundle.getEntry(path), publicStore.value(), sensivity, SPACE.PUBLIC);
			} else
			if (path.equals("CHERRY/private/")) {
				doExport(bundle,bundle.getEntry(path), privateStore.value(), sensivity, SPACE.PRIVATE);
			}
		}
		File temp = new File( tempStore.value(), bundle.getSymbolicName() );
		temp.mkdirs();
		updateDeployDescriptor(bundle.getSymbolicName(), temp, null, SPACE.TEMP);
	
	}

	private void doCleanup(File root) {
		MFile.deleteDir(root);
	}

	private void doExport(Bundle bundle, URL entry, File file, SENSIVITY sensivity, SPACE space) {
		log.i("Export",file, bundle.getSymbolicName());
		
		String path = entry.getPath();
		
		MProperties config = null;
		String configPath = path + "content.properties";
		URL configEntry = bundle.getEntry(configPath);
		if (configEntry != null) {
			try {
				InputStream is = configEntry.openStream();
				config = MProperties.load(is);
				is.close();
			} catch (Throwable t) {}
		}
		if (config == null) config = new MProperties();
		
		file = new File(file, config.getString("name", bundle.getSymbolicName() ));
		
		updateDeployDescriptor(bundle.getSymbolicName(), file, config, space);

		if (sensivity.ordinal() <= SENSIVITY.CHECK.ordinal() || file.exists() && sensivity.ordinal() <= SENSIVITY.WRITE.ordinal() ) return;
		if (sensivity.ordinal() > SENSIVITY.OVERWRITE.ordinal()) doCleanup(file);

		Enumeration<String> list = bundle.getEntryPaths(path);
		while (list.hasMoreElements()) {
			String path2 = list.nextElement();
			if (!path2.endsWith("/content.properties"))
				doExport(bundle,path2, path.length()-1, file, config, sensivity.ordinal() <= SENSIVITY.UPDATE.ordinal());
		}
		
		
	}

	private synchronized void updateDeployDescriptor(String name, File file, MProperties config, SPACE space) {
		MyDeployDescriptor descriptor = descriptors.get(name);
		if (descriptor == null) {
			descriptor = new MyDeployDescriptor(name);
			descriptors.put(name, descriptor);
		}
		descriptor.doUpdate(space,file,config);
	}

	private void doExport(Bundle bundle, String path, int prefixLength, File root, MProperties config, boolean update) {
		log.d("File",path);
		if (path.endsWith("/")) {
			Enumeration<String> list = bundle.getEntryPaths(path);
			while (list.hasMoreElements()) {
				String path2 = list.nextElement();
				doExport(bundle,path2, prefixLength, root, config, update);
			}
		} else {
							
			FileDeployer deployer = Sop.getApi(CherryApi.class).findFileDeployer(MFile.getFileSuffix(path));
			URL entry = bundle.getEntry(path);
			if (deployer != null) {
				deployer.doDeploy(root, path.substring(prefixLength), entry, config);
			} else {
				File f = new File(root, path.substring(prefixLength));
//				if (f.exists() && update && ) return; // Update is not working, have no modify date of the origin
				
				f.getParentFile().mkdirs();
				try {
					InputStream is = entry.openStream();
					FileOutputStream os = new FileOutputStream(f);
					MFile.copyFile(is, os);
					is.close();
					os.close();
				} catch (Throwable t) {
					log.w(path,f,t);
				}
			}
		}
	}

	public void refreshAll(SENSIVITY sensivity) {
		
		if (sensivity == SENSIVITY.RESET) {
			log.i("Delete Delivery Stores");
			MFile.deleteDir(privateStore.value());
			privateStore.value().mkdirs();
			MFile.deleteDir(publicStore.value());
			publicStore.value().mkdirs();
		}
		
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getState() == Bundle.ACTIVE) {
				try {
					doBundle(bundle, sensivity);
				} catch (Throwable t) {
					log.w("can't refresh bundle",bundle.getSymbolicName(),t);
				}
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File file = getResource(req);
		if (file == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		doHead(file, resp);
		FileInputStream is = new FileInputStream(file);
		ServletOutputStream os = resp.getOutputStream();
		MFile.copyFile(is, os);
		is.close();
		os.close();
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File file = getResource(req);
		if (file == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		doHead(file, resp);
	}

	private void doHead(File file, HttpServletResponse resp) {
		resp.setDateHeader("Date", System.currentTimeMillis());
		resp.setDateHeader("Last-Modified", file.lastModified());
		String type = getServletContext().getMimeType(file.getName());
		resp.setContentType(type);
	}

	private File getResource(HttpServletRequest req) {
		String path = req.getPathInfo();
		File file = new File(publicStore.value(), path);
		if (file.isDirectory() || !file.exists() || file.isHidden() || file.getName().startsWith(".")) return null;
		return file;
	}

	private static class MyDeployDescriptor implements DeployDescriptor {
		
		File[] pathes = new File[SPACE.values().length];
		MProperties[] configs = new MProperties[pathes.length];
		private String name;
		
		public MyDeployDescriptor(String name) {
			this.name = name;
		}

		public void doUpdate(SPACE space, File file, MProperties config) {
			pathes[space.ordinal()] = file;
			configs[space.ordinal()] = config;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public File getPath(SPACE space) {
			return pathes[space.ordinal()];
		}

		@Override
		public MProperties getConfiguration(SPACE space) {
			return configs[space.ordinal()];
		}

		@Override
		public String getWebPath(SPACE space) {
			File f = pathes[space.ordinal()];
			if (f == null) return null;
			return "/.pub/" + f.getName();
		}
		
	}

	public DeployDescriptor getDescriptor(String name) {
		return descriptors.get(name);
	}
	
	public List<DeployDescriptor> getDescriptors() {
		LinkedList<DeployDescriptor> out = new LinkedList<>();
		for (MyDeployDescriptor item : descriptors.values() )
			out.add(item);
		return out;
	}
	
	
}
