package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get")
public class DefaultGetRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(VirtualHost vHost, HttpServletRequest req, HttpServletResponse res, String retType, CaoNode navResource, CaoNode resResource) throws IOException {
	
		DefaultHeadRenderer.doRenderHead(vHost, req, res, retType, navResource, resResource);

		if (resResource.hasContent()) {
			InputStream is = resResource.getInputStream();
			ServletOutputStream os = res.getOutputStream();
			MFile.copyFile(is, os);
			is.close();
		}
		
	}

}
