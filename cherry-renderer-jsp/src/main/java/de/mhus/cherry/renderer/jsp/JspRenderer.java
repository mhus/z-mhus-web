package de.mhus.cherry.renderer.jsp;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.ScriptRenderer;

// http://stackoverflow.com/questions/1719254/jsp-programmatically-render#1719398

@Component(provide = ScriptRenderer.class, name="cherry_script_renderer_jsp")
public class JspRenderer implements ScriptRenderer {

	public static final String NAME = "processor_jsp_context";

	@Override
	public void doRender(CallContext call, File file) throws Exception {
		JspContext ctx = (JspContext) call.getSessionContext().getAttribute(NAME);
		if (ctx == null) {
			try {
				ctx = new JspContext(file);
				call.getSessionContext().setAttribute(NAME, ctx);
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
