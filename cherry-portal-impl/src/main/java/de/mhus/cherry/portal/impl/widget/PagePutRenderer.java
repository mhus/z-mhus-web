package de.mhus.cherry.portal.impl.widget;

import java.util.Set;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.core.MLog;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_put_page")
public class PagePutRenderer extends MLog implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		
	}

	@Override
	public void doCollectResourceLinks(String name, Set<String> list) {
	}

}
