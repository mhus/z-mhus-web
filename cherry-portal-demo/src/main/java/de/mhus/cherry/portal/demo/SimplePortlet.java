package de.mhus.cherry.portal.demo;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.control.EditorFactory.TYPE;
import de.mhus.cherry.portal.api.portlet.Widget;

@Component(
		properties = {
				"javax.portlet.display-name=PortletTest01 Portlet",
				"javax.portlet.init-param.template-path=/",
				"javax.portlet.init-param.view-template=/view.jsp",
				"javax.portlet.resource-bundle=content.Language",
				"javax.portlet.security-role-ref=power-user"
		},
		provide=Portlet.class
		)
@Widget(
		displayName="PortletTest01 Portlet",
		type = TYPE.WIDGET,
		editor=SimpleEditorFactory.class
		)
public class SimplePortlet implements Portlet {

	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
