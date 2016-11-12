package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.osgi.sop.api.rest.JsonResult;

public abstract class AbstractActionRenderer extends MLog implements ResourceRenderer {

	// Create = POST to a base URI returning a newly created URI
	
	@Override
	public void doRender(CallContext call) throws IOException {
		
		JsonResult json = new JsonResult();
		ObjectNode out = json.createObjectNode();
		try {
			OperationResult res = doAction(call);
			if (res == null) return;
			
			out.put("msg", res.getMsg());
			out.put("success", res.isSuccessful());
			if (!res.isSuccessful())
				out.put("_error", res.getMsg());
			out.put("rc", res.getReturnCode());
			
			out.put("result", MJson.pojoToJson(res.getResult()) );
			
		} catch (Throwable e) {
			out.put("_error", e.toString());
			out.put("success", false);
		}
		try {
			HttpServletResponse response = call.getHttpResponse();
//			response.setHeader("Cache-Control","max-age=0");
			response.setHeader("Cache-Control","no-cache, must-revalidate"); //HTTP 1.1
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Expires","Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
			  
			response.setContentType(call.getHttpResponse().getContentType());
			json.write(call.getHttpResponse().getWriter());
		} catch (Exception e) {
			log().w(e); // should not happen
		}

	}
	
	protected abstract OperationResult doAction(CallContext call) throws Exception;
	
	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		
	}

}
