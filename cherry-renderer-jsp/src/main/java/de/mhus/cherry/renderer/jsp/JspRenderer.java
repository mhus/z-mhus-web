package de.mhus.cherry.renderer.jsp;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.DeployDescriptor;
import de.mhus.cherry.portal.api.DeployDescriptor.SPACE;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.Sop;

// http://stackoverflow.com/questions/1719254/jsp-programmatically-render#1719398

@Component(provide = ScriptRenderer.class, name="cherry_script_renderer_jsp")
public class JspRenderer extends MLog implements ScriptRenderer {

	public static final String NAME = "processor_jsp_context";

	@Override
	public void doRender(CallContext call, Bundle bundle, File file) throws Exception {
		
		DeployDescriptor descriptor = Sop.getApi(CherryApi.class).getDeployDescritor(bundle);
		InternalCherryApi internal = Sop.getApi(InternalCherryApi.class);
		File root = descriptor.getPath(SPACE.PRIVATE);
		File tmp = descriptor.getPath(SPACE.TEMP);
	 	
		JspRendererContext ctx = null;
		try {
			ctx = (JspRendererContext) internal.getBundleStore(bundle).get(NAME);
//			ctx = (JspRendererContext) call.getSession().get(NAME + root.getName());
		} catch (ClassCastException e) {}
		if (ctx == null) {
			try {
				log().i("Create JSP Context",call, root.getName());
				ctx = new JspRendererContext(root, tmp);
				internal.getBundleStore(bundle).put(NAME, ctx);
//				call.getSession().put(NAME + root.getName(), ctx);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		if (ctx == null) {
			call.getVirtualHost().sendError(call,HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		ctx.processRequest(call, file);

	}

}
