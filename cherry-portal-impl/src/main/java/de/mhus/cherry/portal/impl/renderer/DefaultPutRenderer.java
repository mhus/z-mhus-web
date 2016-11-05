package de.mhus.cherry.portal.impl.renderer;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_put")
public class DefaultPutRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Create = PUT with a new URI (not supported here!)
	// Update = PUT with an existing URI
	
	@Override
	protected OperationResult doAction(CallContext call) throws Exception {

		CaoNode res = call.getResource();
		String[] s = call.getSelectors();
		if (s != null && s.length > 0 && "nav".equals(s[0]))
			res = call.getNavigationResource();

		CaoWritableElement wRes = res.getWritableNode();
		
		HttpServletRequest req = call.getHttpRequest();
		for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
			String[] v = entry.getValue();
			if (v.length > 0)
				wRes.setString(entry.getKey(), v[0]);
		}
		
		return wRes.getUpdateAction().doExecute(null);
		
	}


}
