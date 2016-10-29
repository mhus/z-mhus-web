package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class EditorTagHandler extends TagSupport {
	
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		boolean editorMode = "true".equals(((HttpServletRequest)pageContext.getRequest()).getSession().getAttribute("__cherry_edit_mode"));
		if (editorMode) {
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}
}
