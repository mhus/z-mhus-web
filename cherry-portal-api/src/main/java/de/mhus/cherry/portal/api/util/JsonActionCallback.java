package de.mhus.cherry.portal.api.util;

import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.ActionCallback;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.rest.JsonResult;

public abstract class JsonActionCallback extends MLog implements ActionCallback {

	@Override
	public void doAction(CallContext call, CaoNode widget) {
		JsonResult result = new JsonResult();
		try {
			doAction(call, result);
		} catch (Throwable t) {
			log().d(t);
			result.createObjectNode().put("_error", "Internal Server Error");
		}
		try {
			HttpServletResponse response = call.getHttpResponse();
//			response.setHeader("Cache-Control","max-age=0");
			response.setHeader("Cache-Control","no-cache, must-revalidate"); //HTTP 1.1
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Expires","Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
			  
			response.setContentType(result.getContentType());
			result.write(call.getHttpResponse().getWriter());
		} catch (Exception e) {
			log().w(e); // should not happen
		}
	}

	protected abstract void doAction(CallContext call, JsonResult result);

}
