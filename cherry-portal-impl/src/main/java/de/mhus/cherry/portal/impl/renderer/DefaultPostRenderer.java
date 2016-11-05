package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post")
public class DefaultPostRenderer extends MLog implements ResourceRenderer {

	// Create = POST to a base URI returning a newly created URI
	
	@Override
	public void doRender(CallContext call) throws IOException {
	
		CaoNode res = call.getResource();
				
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
