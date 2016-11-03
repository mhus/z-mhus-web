package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;

public class CurrentUserTagHandler extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		
		try {
			AaaContext current = Sop.getApi(AccessApi.class).getCurrentOrGuest();
			pageContext.getOut().print( current.getAccount().getDisplayName() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

}
