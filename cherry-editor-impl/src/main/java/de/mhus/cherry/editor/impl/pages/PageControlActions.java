package de.mhus.cherry.editor.impl.pages;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoNode;

@Component
public class PageControlActions implements PageControlFactory {

	@Override
	public String getName() {
		return "Actions";
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
		public void doUpdate(CaoNode nav, CaoNode res) {
			removeAllComponents();
			if (nav != null) {
				boolean first = true;
				for (CaoAction action : nav.getConnection().getActions()) {
					if (first) {
						Label label = new Label("Navigation");
						addComponent(label);
						first = false;
					}
					Button b = new Button(action.getName());
					b.setWidth("100%");
					addComponent(b);
				}
			}
			
			if (res != null) {
				boolean first = true;
				for (CaoAction action : res.getConnection().getActions()) {
					if (first) {
						Label label = new Label("Resource");
						addComponent(label);
						first = false;
					}
					Button b = new Button(action.getName());
					b.setWidth("100%");
					addComponent(b);
				}
			}
		}
		
	}
}
