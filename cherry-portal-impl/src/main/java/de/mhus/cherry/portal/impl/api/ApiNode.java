package de.mhus.cherry.portal.impl.api;

import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.core.MLog;

public class ApiNode extends MLog implements ResourceRenderer {
	
	protected HashMap<String, ResourceRenderer> apiProvider = new HashMap<>();
	
	@Override
	public void doRender(CallContext call) throws Exception {
		String functionName = call.consumePath();
		if (functionName == null) {
			call.getVirtualHost().sendError(call, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ResourceRenderer function = apiProvider.get(functionName);
		if (function == null) {
			call.getVirtualHost().sendError(call, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		function.doRender(call);
		
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
		// not implemented
	}

}
