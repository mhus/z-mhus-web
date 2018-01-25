package de.mhus.cherry.portal.impl.api;

import org.codehaus.jackson.node.ObjectNode;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.InternalCherryApi;
import de.mhus.cherry.portal.api.util.JsonResourceRenderer;
import de.mhus.lib.core.MApi;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.rest.JsonResult;

public class LogoutHandler extends JsonResourceRenderer {

	@Override
	protected void doRender(CallContext call, JsonResult result) throws Exception {
		
		ObjectNode res = result.createObjectNode();
		InternalCherryApi intern = MApi.lookup(InternalCherryApi.class);
		String ret = intern.doLogout();
		if (ret != null) {
			res.put("_error", ret);
			res.put("successful", false);
		} else {
			res.put("successful", true);
		}

	}


}
