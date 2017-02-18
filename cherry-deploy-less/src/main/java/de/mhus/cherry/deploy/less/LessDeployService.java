package de.mhus.cherry.deploy.less;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.lesscss.LessCompiler;
import org.lesscss.LessException;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;

@Component(provide = FileDeployer.class, name="cherry_file_deployer_less")
public class LessDeployService extends MLog implements FileDeployer {

	LessCompiler lessCompiler = new LessCompiler(Arrays.asList("--relative-urls"));
	
	@Override
	public void doDeploy(File root, String path, URL entry, MProperties config) {
		File f = new File(root, path);
		root = f.getParentFile();
		root.mkdirs();
		File out = new File(root, MFile.replaceSuffix(f.getName(), "css") );
		
		try {
			lessCompiler.compile(f, out);
		} catch (IOException e) {
			log().e(f,e);
		} catch (LessException e) {
			e.printStackTrace();
		}
	}

}
