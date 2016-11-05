package de.mhus.cherry.portal.impl.renderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.DeleteRenditionConfiguration;
import de.mhus.lib.cao.action.UploadRenditionConfiguration;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post_rendition")
public class RenditionPostRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Create = POST to a base URI returning a newly created URI
	

	@Override
	public OperationResult doAction(CallContext call) throws Exception {
		CaoNode res = call.getResource();
		
		CaoAction action = res.getConnection().getActions().getAction(CaoAction.UPLOAD_RENDITION);
		CaoList list = new CaoList(null);
		list.add(res);
		DefaultMonitor monitor = new DefaultMonitor(getClass());
		CaoConfiguration configuration = action.createConfiguration(list, null);
		configuration.getProperties().setString(UploadRenditionConfiguration.RENDITION, call.getSelectors()[0]);
		
		Part part = call.getHttpRequest().getPart("rendition");
		File tmpFile = File.createTempFile("cherry_post_", ".dat");
		InputStream is = part.getInputStream();
		FileOutputStream os = new FileOutputStream(tmpFile);
		MFile.copyFile(is, os);
		is.close();
		os.close();
		configuration.getProperties().setString(UploadRenditionConfiguration.FILE, tmpFile.getAbsolutePath());
		
		OperationResult result = action.doExecute(configuration, monitor);
		
		tmpFile.delete();
		
		return result;
	}

}