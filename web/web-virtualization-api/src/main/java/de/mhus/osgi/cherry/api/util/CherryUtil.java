package de.mhus.osgi.cherry.api.util;

import java.io.File;

public class CherryUtil {

	public static File getTempDir() {
		String tempDir=System.getProperty("java.io.tmpdir");
		File cherryTmp = new File(tempDir,"cherry");
		cherryTmp.mkdirs();
		return cherryTmp;
	}
	
}
