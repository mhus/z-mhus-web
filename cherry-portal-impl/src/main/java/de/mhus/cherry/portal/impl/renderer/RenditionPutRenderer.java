package de.mhus.cherry.portal.impl.renderer;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.strategy.OperationResult;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_put_rendition")
public class RenditionPutRenderer extends RenditionPostRenderer {

	// Create = PUT with a new URI (not supported here!)
	// Update = PUT with an existing URI

	// The same then POST

}
