package de.mhus.cherry.portal.impl.api;

import java.util.Set;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.util.JsonResourceRenderer;
import de.mhus.lib.core.IProperties;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.rest.JsonResult;

public class LogoutHandler extends JsonResourceRenderer {

	@Override
	protected void doRender(CallContext call, JsonResult result) throws Exception {
		ObjectNode res = result.createObjectNode();
		String sessionId = call.getHttpRequest().getSession().getId();
		IProperties session = Sop.getApi(CherryApi.class).getCherrySession(sessionId);
		if (session.get(CherryApi.SESSION_ACCESS_NAME) == null) {
			res.put("_error", "not logged in");
			res.put("successful", false);
			return;
		}
		session.remove(CherryApi.SESSION_ACCESS_NAME);

		res.put("successful", true);

	}


}
