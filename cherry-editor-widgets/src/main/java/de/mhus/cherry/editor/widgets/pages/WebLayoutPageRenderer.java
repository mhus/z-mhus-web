package de.mhus.cherry.editor.widgets.pages;

import java.io.File;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.FileResolver;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.editor.widgets.pages.WebContentPage")
public class WebLayoutPageRenderer  extends MLog implements ResourceRenderer {

	private FileResolver resolver;

	@Override
	public void doRender(CallContext call) throws Exception {
		ResourceRenderer theme = call.getVirtualHost().lookupTheme( call.getNavigationResource() );
		if (theme != null) {
			call.setAttribute(WidgetApi.CURRENT_THEME_SCOPE, WidgetApi.THEME_SCOPE_HEADER);
			theme.doRender(call);
		}
		if (resolver == null) {
			Bundle bundle = FrameworkUtil.getBundle(WebLayoutPageRenderer.class);
			resolver = call.getVirtualHost().getPrivateFileResolver(bundle);
		}
		File file = resolver.getRoot();
		ScriptRenderer renderer =  CherryUtil.getScriptRenderer(call, file);
		renderer.doRender(call, FrameworkUtil.getBundle(WebLayoutPageRenderer.class), file);

		if (theme != null) {
			call.setAttribute(WidgetApi.CURRENT_THEME_SCOPE, WidgetApi.THEME_SCOPE_FOOTER);
			theme.doRender(call);
		}
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		// TODO Auto-generated method stub
		
	}

}
