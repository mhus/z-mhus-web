package de.mhus.cherry.portal.impl.renderer;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_delete")
public class DefaultDeleteRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Delete = DELETE
	
	@Override
	protected OperationResult doAction(CallContext call) throws Exception {
		CaoNode res = call.getResource();
		IProperties s = call.getSelectors();
		if (s != null && "nav".equals(s.getString("resource", "")) )
			res = call.getNavigationResource();

		CaoAction action = res.getConnection().getActions().getAction(CaoAction.DELETE);
		CaoList list = new CaoList(null);
		list.add(res);
		DefaultMonitor monitor = new DefaultMonitor(getClass());
		CaoConfiguration configuration = action.createConfiguration(list, null);
		
		OperationResult result = action.doExecute(configuration, monitor);
		return result;
	}

}
