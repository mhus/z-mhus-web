package de.mhus.cherry.portal.api;

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

}
