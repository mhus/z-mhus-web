package de.mhus.cherry.portal.impl.widget;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoConst;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_head_page")
public class PageHeadRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws IOException {
		doRenderHead(call);
	}

	public static void doRenderHead(CallContext call) throws IOException {
	
		call.getHttpResponse().setDateHeader("Date", System.currentTimeMillis());
		
		Date modified = call.getResource().getDate(CaoConst.MODIFIED);
		if (modified != null) call.getHttpResponse().setDateHeader("Last-Modified", System.currentTimeMillis() );
		
//		String contentType = call.getResource().getString("contentType", null);
//		if (contentType == null) contentType = call.getHttpServlet().getServletContext().getMimeType("a." + call.getReturnType());
//		if (contentType == null) contentType = call.getHttpServlet().getServletContext().getMimeType(call.getResource().getName());
//		if (contentType == null) contentType = call.getVirtualHost().getDefaultContentType();
//		if (contentType != null) call.getHttpResponse().setContentType(contentType);
		call.getHttpResponse().setContentType("text/html");
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}
}
