package de.mhus.cherry.portal.api.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class CherryPortletBase implements Portlet {

	public static final String WIDGET_ANNOTATION = "_cherry_widget_annotation";

	@Override
	public void init(PortletConfig config) throws PortletException {
		Widget widgetAnno = getClass().getAnnotation(Widget.class);
		if (widgetAnno != null)
			config.getPortletContext().setAttribute(WIDGET_ANNOTATION, widgetAnno);
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		
	}

	@Override
	public void destroy() {
		
	}

}
