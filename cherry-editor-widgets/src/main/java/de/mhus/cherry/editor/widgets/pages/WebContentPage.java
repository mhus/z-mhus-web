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
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.editor.widgets.pages.WebContentPage")
public class WebContentPage  extends MLog implements ResourceRenderer {

	private Bundle bundle = FrameworkUtil.getBundle(WebContentPage.class);

	@Override
	public void doRender(CallContext call) throws Exception {
		String themeName = call.getVirtualHost().getContentNodeResolver().getRecursiveString(call.getNavigationResource(), WidgetApi.THEME );
		ResourceRenderer theme = null;
		if (MString.isSet(themeName)) {
			theme = call.getVirtualHost().getResourceRenderer(themeName);
			call.setAttribute(WidgetApi.CURRENT_THEME_SCOPE, WidgetApi.THEME_SCOPE_HEADER);
			theme.doRender(call);
		}
		File file = call.getVirtualHost().getPrivateFile(bundle,"");
		ScriptRenderer renderer =  CherryUtil.getScriptRenderer(call, file);
		renderer.doRender(call, FrameworkUtil.getBundle(WebContentPage.class), file);

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
