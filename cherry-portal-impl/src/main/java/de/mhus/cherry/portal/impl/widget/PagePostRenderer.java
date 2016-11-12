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

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post_page")
public class PagePostRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = call.getResource();
		DefaultHeadRenderer.doRenderHead(call);
		
		Sop.getApi(WidgetApi.class).doAction(call, res);

	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
	}

}
