package de.mhus.cherry.portal.impl.renderer;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.ResourceRenderer;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_put_rendition")
public class RenditionPutRenderer extends RenditionPostRenderer {

	// Create = PUT with a new URI (not supported here!)
	// Update = PUT with an existing URI

	// The same then POST

}
