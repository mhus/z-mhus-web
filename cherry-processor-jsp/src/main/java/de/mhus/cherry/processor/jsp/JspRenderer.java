package de.mhus.cherry.processor.jsp;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_get_jsp")
public class JspRenderer implements ResourceRenderer {

	@Override
	public void doRender(CallContext call) throws Exception {
		
	}

}
