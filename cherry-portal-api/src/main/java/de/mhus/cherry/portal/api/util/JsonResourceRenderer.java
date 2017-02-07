package de.mhus.cherry.portal.api.util;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.AuthResourceRenderer;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.osgi.sop.api.rest.JsonResult;

public abstract class JsonResourceRenderer extends AuthResourceRenderer {

	@Override
	public final void doRender2(CallContext call) {
		JsonResult result = new JsonResult();
		try {
			doRender(call, result);
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

	protected abstract void doRender(CallContext call, JsonResult result) throws Exception;
	
	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		// TODO Auto-generated method stub
		
	}

}
