package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;

public class MeTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		
		try {
			CallContext call = (CallContext)pageContext.getAttribute("call");
			CaoNode nav = call.getNavigationResource().getNav();
			pageContext.getOut().print(nav.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	
}
