package de.mhus.cherry.portal.impl.page;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.util.WidgetUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simplewidget")
public class SimpleWidget extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = WidgetUtil.getResource(call);
		String title = res.getString("title");
		call.getHttpResponse().getWriter().println("<h2>Widget:"+title+"</h2>");
	}

}
