package de.mhus.cherry.portal.impl.renderer;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.DeleteRenditionConfiguration;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_delete_rendition")
public class RenditionDeleteRenderer extends AbstractActionRenderer implements ResourceRenderer {

	// Delete = DELETE
	
	@Override
	protected OperationResult doAction(CallContext call) throws Exception {
		CaoNode res = call.getResource();
		
		CaoAction action = res.getConnection().getActions().getAction(CaoAction.DELETE_RENDITION);
		CaoList list = new CaoList(null);
		list.add(res);
		DefaultMonitor monitor = new DefaultMonitor(getClass());
		CaoConfiguration configuration = action.createConfiguration(list, null);
		configuration.getProperties().setString(DeleteRenditionConfiguration.RENDITION, call.getSelectors().getString("0", null));
		
		OperationResult result = action.doExecute(configuration, monitor);
		return result;
	}

}
