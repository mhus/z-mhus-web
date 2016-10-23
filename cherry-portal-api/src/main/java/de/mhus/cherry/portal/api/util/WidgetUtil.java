package de.mhus.cherry.portal.api.util;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.directory.ResourceNode;

public class WidgetUtil {

	public static final String CURRENT_WIDGET_NODE = "current_widget_node";
	public static final String RENDERER = "renderer";

	public static void doRender(CallContext call, ResourceNode widget) throws Exception {
		String rendererName = widget.getString(RENDERER);
		ResourceRenderer renderer = call.getVirtualHost().getRenderer(rendererName);
		Object saved = call.getAttribute(CURRENT_WIDGET_NODE);
		call.setAttribute(WidgetUtil.CURRENT_WIDGET_NODE, widget);
		renderer.doRender(call);
		call.setAttribute(WidgetUtil.CURRENT_WIDGET_NODE, saved);
	}

	public static CaoNode getResource(CallContext call) {
		return (CaoNode) call.getAttribute(CURRENT_WIDGET_NODE);
	}
	
}
