package de.mhus.cherry.renderer.jsp;

import java.io.File;

import de.mhus.lib.core.util.FileResolver;

public class JspDefaultServletContext extends DefaultServletContext {
	
	// http://www.e-pde.gr/docs/jasper-howto.html
	public JspDefaultServletContext(FileResolver root, File tmp ) {
		super(root);
//		File tmp = new File( host.getTmpRoot(), "jsp");
//		tmp.mkdirs();
		param.put("scratchdir",  tmp.getAbsolutePath() );
		param.put("keepgenerated", "true");
		param.put("enablePooling", "false");
		param.put("cachingAllowed","false");
		param.put("antiResourceLocking","false");
	}
}
