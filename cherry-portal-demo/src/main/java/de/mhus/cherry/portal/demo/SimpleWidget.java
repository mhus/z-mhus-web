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
import de.mhus.lib.core.util.FileResolver;
import de.mhus.osgi.sop.api.Sop;

//@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simplewidget")
public class SimpleWidget extends MLog implements ResourceRenderer {

	private Bundle bundle = FrameworkUtil.getBundle(SimpleWidget.class);
	private FileResolver resolver;
	
	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
//		String title = res.getString("title");
//		call.getHttpResponse().getOutputStream().println("<h2>Widget:"+title+"</h2>");
		
		if (resolver == null) {
			Bundle bundle = FrameworkUtil.getBundle(SimpleWidget.class);
			resolver = call.getVirtualHost().getPrivateFileResolver(bundle);
		}
		File file = resolver.getFile("script/widget.jsp");
		ScriptRenderer renderer = CherryUtil.getScriptRenderer(call, file);
		renderer.doRender(call, bundle, file);
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
