package de.mhus.cherry.processor.jsp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;

// http://stackoverflow.com/questions/1719254/jsp-programmatically-render#1719398

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_jsp")
public class JspRenderer implements ResourceRenderer {

	public static final String NAME = "processor_jsp_context";

	@Override
	public void doRender(CallContext call) throws Exception {
		JspContext ctx = (JspContext) call.getSessionContext().getAttribute(NAME);
		if (ctx == null) {
			try {
				ctx = new JspContext(call.getVirtualHost());
				call.getSessionContext().setAttribute(NAME, ctx);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		if (ctx == null) {
			call.getVirtualHost().sendError(call,HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		return ctx.processRequest(call,call.getResource());

	}

}
