package de.mhus.cherry.renderer.jsp.tagext;

import javax.servlet.jsp.PageContext;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;

public class Util {

	public static CaoNode findRes(CaoNode res, String name, PageContext pageContext) {
		if (res == null && name != null) {
			if (name.startsWith("#nav:")) {
				CallContext call = (CallContext)pageContext.getAttribute("call");
				res = call.getVirtualHost().getNavigationResource(name.substring(5));
			} else
			if (name.equals("#nav")) {
				CallContext call = (CallContext)pageContext.getAttribute("call");
				res = call.getNavigationResource();
			} else
			if (name.equals("#mainres")) {
				CallContext call = (CallContext)pageContext.getAttribute("call");
				res = call.getMainResource();
			} else
			if (name.equals("#page")) {
				CallContext call = (CallContext)pageContext.getAttribute("call");
				res = call.getResource();
			} else
			if (name.equals("#res")) {
				res = (CaoNode) pageContext.getAttribute("res");
			} else
				res = (CaoNode) pageContext.getAttribute(name);
		}
		return res;
	}

}
