package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class NavigationNodeTagHandler extends TagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		
		Navigation nav = NavigationStack.getStack(pageContext).getCurrent();
		if (nav.getNodes().size() != 0)
			return EVAL_BODY_INCLUDE;
		else
			return SKIP_BODY;
	}
}