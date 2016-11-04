package de.mhus.cherry.portal.impl.api;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
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
		
		String sessionId = call.getHttpRequest().getSession().getId();
		IProperties session = Sop.getApi(CherryApi.class).getCherrySession(sessionId);
		if (session.get(CherryApi.SESSION_ACCESS_NAME) != null) {
			res.put("_error", "already logged in");
			res.put("successful", false);
			return;
		}
		
		// for secure try to release
		AaaContext current = (AaaContext)session.get(CherryApi.SESSION_ACCESS_NAME);
		AccessApi api = Sop.getApi(AccessApi.class);
		if (current != null) {
			api.release(current);
		}
		
		AaaContext context = api.process(api.createUserTicket(username,password));
		if (context == null) {
			res.put("_error", "wrong user or password");
			res.put("successful", false);
			return;
		}
		session.put(CherryApi.SESSION_ACCESS_NAME, context);

		res.put("successful", true);
		return;

		
	}

}
