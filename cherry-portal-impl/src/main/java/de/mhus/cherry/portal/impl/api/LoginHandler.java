package de.mhus.cherry.portal.impl.api;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.util.JsonResourceRenderer;
import de.mhus.lib.core.IProperties;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AaaContext;
import de.mhus.osgi.sop.api.aaa.AccessApi;
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
