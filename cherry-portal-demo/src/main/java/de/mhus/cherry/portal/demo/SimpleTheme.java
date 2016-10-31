package de.mhus.cherry.portal.demo;

import java.io.File;
import java.util.Set;

import org.osgi.framework.FrameworkUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simpletheme")
public class SimpleTheme implements ResourceRenderer {

	private String webPath;

	@Override
	public void doRender(CallContext call) throws Exception {
		String scope = (String)call.getAttribute(WidgetApi.CURRENT_THEME_SCOPE);

		DeployDescriptor descriptor = Sop.getApi(CherryApi.class).getDeployDescritor(FrameworkUtil.getBundle(SimpleWidget.class).getSymbolicName());
		File root = descriptor.getPath(SPACE.PRIVATE);
		File file = null;
		switch (scope) {
		case WidgetApi.THEME_SCOPE_HEADER:
			file = new File(root, "theme/header.jsp");
		break;
		case WidgetApi.THEME_SCOPE_FOOTER:
			file = new File(root, "theme/footer.jsp");
		break;
		}
		if (file == null || !file.exists()) return;
		ScriptRenderer renderer = call.getVirtualHost().getScriptRenderer("jsp");
		renderer.doRender(call, root, file);
		call.getHttpResponse().flushBuffer(); // really needed?
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		if (webPath == null)
			webPath = Sop.getApi(CherryApi.class).getDeployDescritor(FrameworkUtil.getBundle(SimpleTheme.class).getSymbolicName()).getWebPath(SPACE.PUBLIC);
		
		switch(name) {
		case ResourceRenderer.RESOURCE_CSS:
			list.add( webPath + "/css/main.css" );
		break;
		case ResourceRenderer.RESOURCE_JAVASCRIPT:
			list.add( webPath + "/js/main.js" );
		break;
		}
	}

}
