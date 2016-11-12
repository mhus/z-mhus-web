package de.mhus.cherry.portal.impl.renderer;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.ResourceRenderer;

@Component(provide = ResourceRenderer.class, name="cherry_renderer_post_json")
public class DefaultPostJsonRenderer extends DefaultPostRenderer {

}
