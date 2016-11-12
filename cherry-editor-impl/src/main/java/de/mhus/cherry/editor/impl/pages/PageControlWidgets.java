package de.mhus.cherry.editor.impl.pages;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.osgi.sop.api.Sop;

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

		private ControlParent controlParent;

		public Control() {
			
		}

		@Override
		public void doInit(ControlParent controlParent) {
			this.controlParent = controlParent;
		}

		@Override
		public void doClean() {
			removeAllComponents();
		}

		@Override
		public void doUpdate(NavNode nav) {
			removeAllComponents();

			CaoNode content = nav.getRes();
			if (content == null) return;
			
			{
				Button b = new Button();
				b.setHtmlContentAllowed(true);
				b.setCaption("<b>Page</b>");
				b.setWidth("100%");
				addComponent(b);
				addComponent(new Label("---"));
				b.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GuiUtil.getApi().navigateToEditor( content );
					}
				});
			}
			
			for (CaoNode c : Sop.getApi(WidgetApi.class).sortWidgets(content).getNodes()) {
				try {
					Button b = new Button();
					b.setHtmlContentAllowed(true);
					
					String type = c.getString(WidgetApi.RENDERER, null);
					if (MString.isIndex(type, '.')) type = MString.afterLastIndex(type, '.');
					b.setCaption("<b>" + c.getString("title", MXml.encode(c.getName())) + "</b>" + (type == null ? "" : "<br/>" + MXml.encode(type) ) );
					b.setWidth("100%");
					addComponent(b);
					b.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							GuiUtil.getApi().navigateToEditor( c );
						}
					});

				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
						
		}
		
	}
}
