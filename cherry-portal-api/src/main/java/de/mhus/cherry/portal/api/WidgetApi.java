package de.mhus.cherry.portal.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.SApi;

public interface WidgetApi extends SApi {

	public static final String CURRENT_WIDGET_NODE = "current_widget_node";
	public static final String RENDERER = "cherry:renderer";
	public static final String EDITOR = "cherry:editor";
	public static final String THEME = "cherry:theme";
	public static final String CURRENT_THEME_SCOPE = "current_theme_scope";
	public static final String THEME_SCOPE_HEADER = "header";
	public static final String THEME_SCOPE_FOOTER = "footer";
	public static final int MAX_SEARCH_LEVEL = 10;
//	public static final String CONTENT_NODE = "content";
	public static final String RES_TITLE = "title";
	public static final String CONTAINER = "cherry:container";
	public static final String SORT = "cherry:sort";

	void doRender(CallContext call, CaoNode widget) throws Exception;

	CaoNode getResource(CallContext call);

	EditorFactory getControlEditorFactory(VirtualHost vHost, CaoNode resource);

	String getEditorLink(CallContext call, CaoNode res);

	/**
	 * Return a html head content snipped for the resource
	 * 
	 * @param call
	 * @param res
	 * @return
	 */
	String getHtmlHead(CallContext call, CaoNode res);
	
	/**
	 * Filters and sorts the widgets of a page into the defined containers by attribute definitions.
	 * To allow iterating by the taglib it returns a dummy caonode as list. Use getNodes to get the list.
	 * 
	 * @param pageRes
	 * @return Map with container name as key and node list as value
	 */
	Map<String, CaoNode> sortWidgetsIntoContainers(CaoNode pageRes);

	CaoNode sortWidgetsIntoContainers(CaoNode pageRes, String container);
	/**
	 * Filters and sorts the widgets of a page into a single list. 
	 * To allow iterating by the taglib it returns a dummy caonode as list. Use getNodes to get the list.
	 * 
	 * @param pageRes
	 * @return List of the nodes packed in the caonode
	 */
	CaoNode sortWidgets(CaoNode pageRes);

}
