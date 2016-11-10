package de.mhus.cherry.portal.impl.widget;

import java.util.Set;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.impl.renderer.DefaultHeadRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_page")
public class PageGetRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = call.getMainResource();
		DefaultHeadRenderer.doRenderHead(call);
		call.getHttpResponse().setContentType("text/html");
		
		Sop.getApi(WidgetApi.class).doRender(call, res);
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}


}
