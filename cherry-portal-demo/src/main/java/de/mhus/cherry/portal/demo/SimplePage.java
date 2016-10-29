package de.mhus.cherry.portal.demo;

import java.util.Map.Entry;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simplepage")
public class SimplePage extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
		String title = res.getString("title");
		call.getHttpResponse().getOutputStream().println("<h1>" + title + "</h1>");
		
		for (ResourceNode sub : res.getNodes()) {
			try {
				Sop.getApi(WidgetApi.class).doRender(call, sub);
			} catch (Throwable t) {
				log().w(t);
			}
		}
		
		
	}

}
