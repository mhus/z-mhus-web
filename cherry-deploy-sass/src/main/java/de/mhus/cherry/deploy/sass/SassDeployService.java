package de.mhus.cherry.deploy.sass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.FileDeployer;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;

@Component(provide = FileDeployer.class, name="cherry_file_deployer_sass")
public class SassDeployService extends MLog implements FileDeployer {
	
	io.bit3.jsass.Compiler compiler = new io.bit3.jsass.Compiler();
	Options options = new Options();
	
	@Override
	public void doDeploy(File root, String path, URL entry, MProperties config) {
		File f = new File(root, path);
		root = f.getParentFile();
		root.mkdirs();
		File out = new File(root, MFile.replaceSuffix(f.getName(), "css") );
		
		try {
			Output output = compiler.compileFile(f.toURI(), out.toURI(), options);
		} catch (CompilationException e) {
			log().e(f,e);
		}
		
	}

}
