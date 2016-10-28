package de.mhus.cherry.portal.impl;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.directory.ResourceNode;

@Component
public class WidgetApiImpl extends MLog implements WidgetApi {

	public static final String CURRENT_WIDGET_NODE = "current_widget_node";
	public static final String RENDERER = "renderer";

	@Override
	public void doRender(CallContext call, ResourceNode widget) throws Exception {
		String rendererName = widget.getString(RENDERER);
		if (rendererName == null) {
			log().d("renderer not set", call, widget);
			return;
		}
		ResourceRenderer renderer = call.getVirtualHost().getRenderer(rendererName);
		if (renderer == null) {
			log().d("rednerer not found", call, rendererName, widget);
			return;
		}
		Object saved = call.getAttribute(CURRENT_WIDGET_NODE);
		call.setAttribute(CURRENT_WIDGET_NODE, widget);
		renderer.doRender(call);
		call.setAttribute(CURRENT_WIDGET_NODE, saved);
	}

	@Override
	public CaoNode getResource(CallContext call) {
		return (CaoNode) call.getAttribute(CURRENT_WIDGET_NODE);
	}

}
