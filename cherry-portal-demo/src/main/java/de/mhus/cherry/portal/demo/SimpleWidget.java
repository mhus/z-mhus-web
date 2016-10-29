package de.mhus.cherry.portal.demo;

import java.io.File;

import org.osgi.framework.FrameworkUtil;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.Sop;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_de.mhus.cherry.portal.impl.page.simplewidget")
public class SimpleWidget extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
		String title = res.getString("title");
		//call.getHttpResponse().getWriter().println("<h2>Widget:"+title+"</h2>");
		call.getHttpResponse().getOutputStream().println("<h2>Widget:"+title+"</h2>");
		
		DeployDescriptor descriptor = Sop.getApi(CherryApi.class).getDeployDescritor(FrameworkUtil.getBundle(SimpleWidget.class).getSymbolicName());
		File root = descriptor.getPath(SPACE.PRIVATE);
		File file = new File(root, "script/test.jsp");
		ScriptRenderer renderer = call.getVirtualHost().getScriptRenderer("jsp");
		renderer.doRender(call, file);
	}

}
