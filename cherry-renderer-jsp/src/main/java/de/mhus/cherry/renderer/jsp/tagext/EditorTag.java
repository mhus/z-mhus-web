package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MApi;
import de.mhus.osgi.sop.api.Sop;

public class EditorTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		CallContext call = (CallContext) pageContext.getAttribute("call");
		CaoNode res = (CaoNode) pageContext.getAttribute("resource");
		boolean editorMode = MApi.lookup(CherryApi.class).canEditResource(call,res);
		if (!editorMode) return SKIP_BODY;
		return EVAL_BODY_INCLUDE;
	}
}
