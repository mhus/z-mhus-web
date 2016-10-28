package de.mhus.cherry.portal.impl.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.io.Unzip;

@Component(provide = FileDeployer.class, name="cherry_file_deployer_zip")
public class FileDeployUnzipService extends MLog implements FileDeployer {

	@Override
	public void doDeploy(File root, String path, URL entry, MProperties config) {
		File f = new File(root, path);
		root = f.getParentFile();
		root.mkdirs();
		
		try {
			// copy file
			InputStream is = entry.openStream();
			FileOutputStream os = new FileOutputStream(f);
			MFile.copyFile(is, os);
			is.close();
			os.close();
			
			// unzip
			new Unzip().unzip(f, root, null);
			
			// delete zip
			f.delete();
		} catch (Throwable t) {
			log().w(f,t);
		}
	}

}
