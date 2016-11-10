package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get")
public class DefaultGetRenderer extends MLog implements ResourceRenderer {

	// Read   = GET
	
	@Override
	public void doRender(CallContext call) throws IOException {
	
		DefaultHeadRenderer.doRenderHead(call);

		if (call.getResource().hasContent()) {
			String rendition = null;
			IProperties s = call.getSelectors();
			if (s != null) rendition = s.getString("0",null);
			InputStream is = call.getResource().getInputStream(rendition);
			if (is != null) {
				ServletOutputStream os = call.getHttpResponse().getOutputStream();
				MFile.copyFile(is, os);
				is.close();
			} // TODO else?
		}
		
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
