package de.mhus.cherry.portal.demo;

import org.codehaus.jackson.node.ObjectNode;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.ActionCallback;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.util.JsonActionCallback;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.sop.api.rest.JsonResult;

@Component(provide = ActionCallback.class, name="cherry_callback_de.mhus.cherry.portal.impl.page.simplewidget")
public class SimpleWidgetCallback extends JsonActionCallback {

	@Override
	protected void doAction(CallContext call, JsonResult result) {
		ObjectNode ret = result.createObjectNode();
		
		ret.put("free", Runtime.getRuntime().freeMemory() );
		ret.put("total", Runtime.getRuntime().totalMemory() );
		ret.put("max", Runtime.getRuntime().maxMemory() );
		ret.put("processors", Runtime.getRuntime().availableProcessors() );

	}

}
