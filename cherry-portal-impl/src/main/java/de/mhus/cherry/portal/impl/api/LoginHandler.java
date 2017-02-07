package de.mhus.cherry.portal.impl.api;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.util.JsonResourceRenderer;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.rest.JsonResult;

public class LoginHandler extends JsonResourceRenderer {

	@Override
	protected void doRender(CallContext call, JsonResult result) throws Exception {
		
		ObjectNode res = result.createObjectNode();
		String username = call.getHttpRequest().getParameter("username");
		String password = call.getHttpRequest().getParameter("password");
		
		InternalCherryApi intern = Sop.getApi(InternalCherryApi.class);
		String ret = intern.doLogin(username,password);
		if (ret != null) {
			res.put("_error", ret);
			res.put("successful", false);
		} else {
			res.put("successful", true);
		}
		
	}

}
