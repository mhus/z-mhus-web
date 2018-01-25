package de.mhus.cherry.portal.impl.widget;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.ActionCallback;
import de.mhus.cherry.portal.api.CacheApi;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.util.ListCaoNode;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
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
		ResourceRenderer renderer = call.getVirtualHost().getWidgetDescriptor(rendererName).getRenderer();
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
		String editorName = resource.getString(RENDERER, null);
		if (editorName != null)
			return vHost.getWidgetDescriptor(editorName).getEditorFactory();
		return null;
	}

	@Override
	public String getEditorLink(CallContext call, CaoNode res) {
		String path = "!" + call.getHttpPath() + ":editor//" + res.getConnection().getName() + ":" + res.getId();
		return "/.control/editor#" + path;
	}

	@Override
	public String getHtmlHead(CallContext call, CaoNode res) {
		
		CacheApi cache = MApi.lookup(CacheApi.class);
		String val = cache.getString(res, "widget_html_head");
		if (val != null) return val;

		CherryApi api = MApi.lookup(CherryApi.class);
		
		
		HashSet<String> cssList = new HashSet<>();
		HashSet<String> jsList = new HashSet<>();
		
		collectHtmlResources(call,res, cssList, jsList, 0);
		
		String themeName = api.getRecursiveString(call.getNavigationResource().getNav(), WidgetApi.THEME);
		if (themeName != null) {
			ResourceRenderer renderer = call.getVirtualHost().getWidgetDescriptor(themeName).getRenderer();
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
			ResourceRenderer renderer = call.getVirtualHost().getWidgetDescriptor(rendererName).getRenderer();
			if (renderer != null) {
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_JAVASCRIPT, jsList);
				renderer.doCollectResourceLinks(ResourceRenderer.RESOURCE_CSS, cssList);
			}
		}
		for (CaoNode child : res.getNodes())
			collectHtmlResources(call, child, cssList, jsList, level+1);
	}

	@Override
	public Map<String, CaoNode> sortWidgetsIntoContainers(CaoNode pageRes) {
		HashMap<String, CaoNode> out = new HashMap<>();
		// iterate all widgets
		for (CaoNode widget : pageRes.getNodes()) {
			// only nodes with renderer and not hidden are widgets
			if (widget.getString(WidgetApi.RENDERER, null) != null && !widget.getBoolean(CherryApi.NAV_HIDDEN, false)) {
				String container = widget.getString(CONTAINER, "");
				CaoNode listNode = out.get(container);
				if (listNode == null) {
					// not defined yet, create new container list
					TreeSet<CaoNode> list = new TreeSet<>(new Comparator<CaoNode>() {
						@Override
						public int compare(CaoNode o1, CaoNode o2) {
							int c = Integer.compare(o1.getInt(SORT, Integer.MAX_VALUE), o2.getInt(SORT, Integer.MAX_VALUE));
							if (c != 0) return c;
							c = o1.getName().compareTo(o2.getName());
							if (c != 0) return c;
							return Integer.compare(o1.hashCode(), o2.hashCode() );
						}
					});
					listNode = new ListCaoNode(container, list);
					out.put(container, listNode);
				}
				// and add the new node
				listNode.getNodes().add(widget);
			}
		}
		return out;
	}

	@Override
	public CaoNode sortWidgets(CaoNode pageRes) {
		TreeSet<CaoNode> list = new TreeSet<>(new Comparator<CaoNode>() {
			@Override
			public int compare(CaoNode o1, CaoNode o2) {
				int c = o1.getString(CONTAINER, "").compareTo(o2.getString(CONTAINER, ""));
				if (c != 0) return c;
				c = Integer.compare(o1.getInt(SORT, Integer.MAX_VALUE), o2.getInt(SORT, Integer.MAX_VALUE));
				if (c != 0) return c;
				c = o1.getName().compareTo(o2.getName());
				if (c != 0) return c;
				return Integer.compare(o1.hashCode(), o2.hashCode() );
			}
		});
		// iterate all widgets
		for (CaoNode widget : pageRes.getNodes()) {
			// only nodes with renderer and not hidden are widgets
			if (widget.getString(WidgetApi.RENDERER, null) != null && !widget.getBoolean(CherryApi.NAV_HIDDEN, false)) {
				list.add(widget);
			}
		}
		return new ListCaoNode("", list);
	}

	@Override
	public CaoNode sortWidgetsIntoContainers(CaoNode pageRes, String container) {
		TreeSet<CaoNode> list = new TreeSet<>(new Comparator<CaoNode>() {
			@Override
			public int compare(CaoNode o1, CaoNode o2) {
				return Integer.compare(o1.getInt(SORT, Integer.MAX_VALUE), o2.getInt(SORT, Integer.MAX_VALUE));
			}
		});
		// iterate all widgets
		for (CaoNode widget : pageRes.getNodes()) {
			// only nodes with renderer and not hidden are widgets
			if (
					widget.getString(WidgetApi.RENDERER, null) != null && 
					!widget.getBoolean(CherryApi.NAV_HIDDEN, false) && 
					widget.getString(CONTAINER, "").equals(container)) {
				list.add(widget);
			}
		}
		return new ListCaoNode(container, list);
	}

	@Override
	public void doAction(CallContext call, CaoNode widget) throws Exception {
		call.getHttpResponse().flushBuffer();
		String callbackName = widget.getString(ACTION_CALLBACK);
		if (callbackName == null)
			callbackName = widget.getString(RENDERER);
		if (callbackName == null) {
			log().d("renderer not set", call, widget);
			return;
		}
		
		ActionCallback callback = call.getVirtualHost().getActionCallback(callbackName);
		if (callback == null) {
			log().d("callback not found", call, callbackName, widget);
			return;
		}
		
		callback.doAction(call, widget);
		call.getHttpResponse().flushBuffer();
	}

}
