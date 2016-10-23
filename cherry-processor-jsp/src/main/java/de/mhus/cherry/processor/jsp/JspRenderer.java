package de.mhus.cherry.processor.jsp;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_jsp")
public class JspRenderer implements ResourceRenderer {

	public static final String NAME = "jsp";

	@Override
	public void doRender(CallContext call) throws Exception {
		JspContext ctx = (JspContext) call.getVirtualHost().getProcessorContext(NAME);
		if (ctx == null) {
			try {
				ctx = new JspContext(host);
				host.setProcessorContext(ctx);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
		if (ctx == null) {
			context.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return true;
		}
		
		return ctx.processRequest(context,res);

	}

}
