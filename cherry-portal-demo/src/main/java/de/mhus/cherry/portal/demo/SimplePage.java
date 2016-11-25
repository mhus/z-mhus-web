package de.mhus.cherry.portal.demo;

import java.io.File;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simplepage")
public class SimplePage extends MLog implements ResourceRenderer {

	private Bundle bundle = FrameworkUtil.getBundle(SimpleWidget.class);
	
	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
//		String title = res.getString("title");
//		call.getHttpResponse().getOutputStream().println("<h1>" + title + "</h1>");

//		call.setAttribute("children", res.getNodes() );
//		for (ResourceNode sub : res.getNodes()) {
//			try {
//				Sop.getApi(WidgetApi.class).doRender(call, sub);
//			} catch (Throwable t) {
//				log().w(t);
//			}
//		}
		
// no fall back		String themeName = Sop.getApi(CherryApi.class).getRecursiveString(call.getNavigationResource(), WidgetApi.THEME );
		String themeName = call.getVirtualHost().getContentNodeResolver().getRecursiveString(call.getNavigationResource(), WidgetApi.THEME );
		ResourceRenderer theme = null;
		if (MString.isSet(themeName)) {
			theme = call.getVirtualHost().getResourceRenderer(themeName);
			call.setAttribute(WidgetApi.CURRENT_THEME_SCOPE, WidgetApi.THEME_SCOPE_HEADER);
			theme.doRender(call);
		}
		DeployDescriptor descriptor = Sop.getApi(CherryApi.class).getDeployDescritor(bundle);
		File root = descriptor.getPath(SPACE.PRIVATE);
		File file = new File(root, "script/page.jsp");
		ScriptRenderer renderer =  CherryUtil.getScriptRenderer(call, file);
		renderer.doRender(call, FrameworkUtil.getBundle(SimplePage.class), file);

		if (theme != null) {
			call.setAttribute(WidgetApi.CURRENT_THEME_SCOPE, WidgetApi.THEME_SCOPE_FOOTER);
			theme.doRender(call);
		}
		
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
