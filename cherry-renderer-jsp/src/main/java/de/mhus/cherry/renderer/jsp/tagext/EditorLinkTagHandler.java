package de.mhus.cherry.renderer.jsp.tagext;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;

public class EditorLinkTagHandler extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		
		try {
			CallContext call = (CallContext)pageContext.getAttribute("call");
			CaoNode res = (CaoNode)pageContext.getAttribute("resource");
			String path = call.getHttpPath() + ":" + res.getId();
			pageContext.getOut().print("/.contenteditor/editor#" + path );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	
}
