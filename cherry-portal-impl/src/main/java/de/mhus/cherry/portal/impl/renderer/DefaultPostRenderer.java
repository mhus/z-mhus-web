package de.mhus.cherry.portal.impl.renderer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post")
public class DefaultPostRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Create = POST to a base URI returning a newly created URI
	

	@Override
	public OperationResult doAction(CallContext call) throws IOException, CaoException {
	
		CaoNode res = call.getResource();
		String[] s = call.getSelectors();
		if (s != null && s.length > 0 && "nav".equals(s[0]))
			res = call.getNavigationResource();
		
		CaoAction action = res.getConnection().getActions().getAction(CaoAction.CREATE);
		CaoList list = new CaoList(null);
		list.add(res);
		DefaultMonitor monitor = new DefaultMonitor(getClass());
		
		CaoConfiguration configuration = action.createConfiguration(list, null);
		// payload
		HttpServletRequest req = call.getHttpRequest();
		for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
			String[] v = entry.getValue();
			if (v.length > 0)
				configuration.getProperties().setString(entry.getKey(), entry.getValue()[0]);
		}
		
		OperationResult result = action.doExecute(configuration, monitor);
		return result;
	}

}
