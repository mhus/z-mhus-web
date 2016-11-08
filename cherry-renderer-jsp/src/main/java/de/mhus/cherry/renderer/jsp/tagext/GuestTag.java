package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class GuestTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private boolean switz;

	public void setSwitch(boolean switz) {
		this.switz = switz;
	}

	@Override
	public int doStartTag() throws JspException {
		
		AaaContext context = Sop.getApi(AccessApi.class).getCurrent();
		
		if (context == null) {
			return switz ? SKIP_BODY : EVAL_BODY_INCLUDE;
		}
		return switz ? EVAL_BODY_INCLUDE : SKIP_BODY;
		
	}
	
}
