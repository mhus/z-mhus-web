package de.mhus.cherry.portal.impl.page;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.util.WidgetUtil;
import de.mhus.cherry.portal.impl.renderer.DefaultHeadRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.directory.ResourceNode;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_page")
public class PageGetRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = call.getMainResource();
		ResourceNode content = res.getNode("content");
		if (content == null) {
			//TODO
			return;
		}
		DefaultHeadRenderer.doRenderHead(call);
		call.getHttpResponse().setContentType("text/html");
		
		WidgetUtil.doRender(call, content);
	}


}
