package de.mhus.cherry.renderer.jsp.tagext;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.lib.cao.CaoNode;
import de.mhus.osgi.sop.api.Sop;

public class RenderTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private CaoNode res;
	private String name;

	public void setResource(CaoNode res) {
		this.res = res;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int doStartTag() throws JspException {

		res = Util.findRes(res, name, pageContext);

		CallContext call = (CallContext)pageContext.getAttribute("call");
		try {
			pageContext.getOut().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Sop.getApi(WidgetApi.class).doRender(call, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SKIP_BODY;
	}

}
