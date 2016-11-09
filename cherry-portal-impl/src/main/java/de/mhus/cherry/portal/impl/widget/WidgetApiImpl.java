package de.mhus.cherry.portal.impl.widget;

import java.util.HashSet;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CacheApi;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.sop.api.Sop;

@Component
public class WidgetApiImpl extends MLog implements WidgetApi {

	@Override
	public void doRender(CallContext call, CaoNode widget) throws Exception {
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
		if (editorName != null)
			return vHost.getControlEditorFactory(editorName);
		editorName = resource.getString(RENDERER, null);
		if (editorName != null)
			return vHost.getControlEditorFactory(editorName);
		return null;
	}

	@Override
	public String getEditorLink(CallContext call, CaoNode res) {
		String path = call.getHttpPath() + ":" + res.getConnection().getName() + ":" + res.getId();
		return "/.control/editor#" + path;
	}

	@Override
	public String getHtmlHead(CallContext call, CaoNode res) {
		
		CacheApi cache = Sop.getApi(CacheApi.class);
		String val = cache.getString(res, "widget_html_head");
		if (val != null) return val;

		CherryApi api = Sop.getApi(CherryApi.class);
		
		
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

		val = out.toString();
		cache.put(res, "widget_html_head", val);
		
		return out.toString();
	}

	private void collectHtmlResources(CallContext call,CaoNode res, HashSet<String> cssList, HashSet<String> jsList, int level) {
		if (level > MAX_SEARCH_LEVEL) return;
		
		String rendererName = res.getString(WidgetApi.RENDERER, null);
		if (MString.isSet(rendererName)) {
			ResourceRenderer renderer = call.getVirtualHost().getResourceRenderer(rendererName);
			if (renderer != null) {
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_JAVASCRIPT, jsList);
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_CSS, cssList);
			}
		}
		for (CaoNode child : res.getNodes())
			collectHtmlResources(call, child, cssList, jsList, level+1);
	}

}
