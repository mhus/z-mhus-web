package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

public class HtmlHeadTagHandler extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		try {
			CallContext call = (CallContext)pageContext.getAttribute("call");
			CaoNode res = (CaoNode)pageContext.getAttribute("resource");
			String content = Sop.getApi(WidgetApi.class).getHtmlHead(call, res);
			pageContext.getOut().print(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	
}
