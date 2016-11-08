package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

public class LoadTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		
		CallContext call = (CallContext)pageContext.getRequest().getAttribute(CallContext.REQUEST_ATTRIBUTE_NAME);
		CaoNode res = Sop.getApi(WidgetApi.class).getResource(call);
		
		pageContext.setAttribute("call", call);
		pageContext.setAttribute("resource", res);
		
		return SKIP_BODY;
	}
}
