package de.mhus.cherry.editor.impl.pages;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;

@Component
public class PageControlWidgets implements PageControlFactory {

	@Override
	public String getName() {
		return "Widgets";
	}

	@Override
	public PageControl createPageControl() {
		return new Control();
	}

	private static class Control extends PageControl {

		public Control() {
			
		}
		
		@Override
		public void doClean() {
			removeAllComponents();
		}

		@Override
		public void doUpdate(CaoNode nav, CaoNode res) {
			removeAllComponents();

			if (res == null) return;
			
			CaoNode content = res.getNode(WidgetApi.CONTENT_NODE);
			if (content == null) return;
			
			for (CaoNode c : content.getNodes()) {
				try {
					Button b = new Button();
					b.setHtmlContentAllowed(true);
					
					String type = c.getString(WidgetApi.RENDERER, null);
					if (MString.isIndex(type, '.')) type = MString.afterLastIndex(type, '.');
					b.setCaption("<b>" + c.getString("title", MXml.encode(c.getName())) + "</b>" + (type == null ? "" : "<br/>" + MXml.encode(type) ) );
					b.setWidth("100%");
					addComponent(b);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
						
		}
		
	}
}
