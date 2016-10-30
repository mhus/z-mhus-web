package de.mhus.cherry.portal.impl;

import java.io.File;
import java.util.HashSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.EditorFactory;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;

@Component
public class WidgetApiImpl extends MLog implements WidgetApi {

	@Override
	public void doRender(CallContext call, ResourceNode widget) throws Exception {
		call.getHttpResponse().flushBuffer();
		String rendererName = widget.getString(RENDERER);
		if (rendererName == null) {
			log().d("renderer not set", call, widget);
			return;
		}
		ResourceRenderer renderer = call.getVirtualHost().getResourceRenderer(rendererName);
		if (renderer == null) {
			log().d("renderer not found", call, rendererName, widget);
			return;
		}
		Object saved = call.getAttribute(CURRENT_WIDGET_NODE);
		call.setAttribute(CURRENT_WIDGET_NODE, widget);
		renderer.doRender(call);
		call.setAttribute(CURRENT_WIDGET_NODE, saved);
		call.getHttpResponse().flushBuffer();
	}

	@Override
	public CaoNode getResource(CallContext call) {
		return (CaoNode) call.getAttribute(CURRENT_WIDGET_NODE);
	}

	@Override
	public EditorFactory getControlEditorFactory(VirtualHost vHost, CaoNode resource) {
		String editorName = resource.getString(EDITOR, null);
		if (editorName == null) return null;
		return vHost.getControlEditorFactory(editorName);
	}

	@Override
	public String getEditorLink(CallContext call, CaoNode res) {
		try {
			String path = call.getHttpPath() + ":" + res.getConnection().getName() + ":" + res.getId();
			return "/.control/editor#" + path;
		} catch (MException e) {
			return null;
		}
	}

	@Override
	public String getHtmlHead(CallContext call, CaoNode res) {
		
		CherryApi api = Sop.getApi(CherryApi.class);
		
		
		// TODO cache ? and optimize
		HashSet<String> cssList = new HashSet<>();
		HashSet<String> jsList = new HashSet<>();
		
		collectHtmlResources(call,res, cssList, jsList, 0);
		
		String themeName = api.getRecursiveString(call.getNavigationResource(), WidgetApi.THEME);
		if (themeName != null) {
			ResourceRenderer renderer = call.getVirtualHost().getResourceRenderer(themeName);
			if (renderer != null) {
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_JAVASCRIPT, jsList);
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_CSS, cssList);
			}
		}
		
		StringBuffer out = new StringBuffer();
		for (String item : cssList)
			out.append("<link rel=\"stylesheet\" href=\"").append(item).append("\"/>");
			
		for (String item : jsList)
			out.append("<script src=\"").append(item).append("\"></script>");

		return out.toString();
	}

	private void collectHtmlResources(CallContext call,ResourceNode res, HashSet<String> cssList, HashSet<String> jsList, int level) {
		if (level > MAX_SEARCH_LEVEL) return;
		
		String rendererName = res.getString(WidgetApi.RENDERER, null);
		if (MString.isSet(rendererName)) {
			ResourceRenderer renderer = call.getVirtualHost().getResourceRenderer(rendererName);
			if (renderer != null) {
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_JAVASCRIPT, jsList);
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_CSS, cssList);
			}
		}
		for (ResourceNode child : res.getNodes())
			collectHtmlResources(call, child, cssList, jsList, level+1);
	}

}
