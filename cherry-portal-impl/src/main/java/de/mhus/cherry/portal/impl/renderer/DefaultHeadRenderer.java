package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
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
	public void doRender(CallContext call) throws IOException {
		doRenderHead(call);
	}

	public static void doRenderHead(CallContext call) throws IOException {
	
		call.getHttpResponse().setDateHeader("Date", System.currentTimeMillis());
		
		Date modified = call.getResource().getDate("modified");
		if (modified != null) call.getHttpResponse().setDateHeader("Last-Modified", modified.getTime());
		
		String contentType = call.getResource().getString("contentType", null);
		if (contentType == null) contentType = call.getHttpServlet().getServletContext().getMimeType("a." + call.getReturnType());
		if (contentType == null) 
			try {
				contentType = call.getHttpServlet().getServletContext().getMimeType(call.getResource().getName());
			} catch (MException e) {}
		if (contentType == null) contentType = call.getVirtualHost().getDefaultContentType();
		if (contentType != null) call.getHttpResponse().setContentType(contentType);
				
	}
}
