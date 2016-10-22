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
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get")
public class DefaultGetRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws IOException {
	
		DefaultHeadRenderer.doRenderHead(call);

		if (call.getResource().hasContent()) {
			InputStream is = call.getResource().getInputStream();
			ServletOutputStream os = call.getHttpResponse().getOutputStream();
			MFile.copyFile(is, os);
			is.close();
		}
		
	}

}
