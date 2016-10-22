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
import de.mhus.cherry.portal.impl.CherryServlet;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_head")
public class DefaultHeadRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(VirtualHost vHost, HttpServletRequest req, HttpServletResponse res, String retType, CaoNode navResource, CaoNode resResource) throws IOException {
		doRenderHead(vHost, req, res, retType, navResource, resResource);
	}

	public static void doRenderHead(VirtualHost vHost, HttpServletRequest req, HttpServletResponse res, String retType, CaoNode navResource, CaoNode resResource) throws IOException {
	
		res.setDateHeader("Date", System.currentTimeMillis());
		
		Date modified = resResource.getDate("modified");
		if (modified != null) res.setDateHeader("Last-Modified", modified.getTime());
		
		String contentType = resResource.getString("contentType", null);
		if (contentType == null) contentType = CherryServlet.instance.getServletContext().getMimeType("a." + retType);
		if (contentType == null) 
			try {
				contentType = CherryServlet.instance.getServletContext().getMimeType(resResource.getName());
			} catch (MException e) {}
		if (contentType == null) contentType = vHost.getDefaultContentType();
		if (contentType != null) res.setContentType(contentType);
				
	}
}
