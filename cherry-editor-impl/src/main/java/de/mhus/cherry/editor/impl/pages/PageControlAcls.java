package de.mhus.cherry.editor.impl.pages;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.vaadin.easyuploads.UploadField;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.osgi.sop.api.Sop;

@Component
public class PageControlAcls implements PageControlFactory {

	@Override
	public String getName() {
		return "Acls";
	}

	@Override
	public PageControl createPageControl() {
		return new Control();
	}

	private static class Control extends PageControl {

		private static final long serialVersionUID = 1L;

		@Override
		public void doClean() {
			removeAllComponents();
		}

		@Override
		public void doInit(ControlParent controlParent) {
			
		}

		@SuppressWarnings("deprecation")
		@Override
		public void doUpdate(NavNode nav) {
	        
			Map<String, String> acls = Sop.getApi(CherryApi.class).getEffectiveAcls(nav.getNav());
			TreeMap<String, String> sorted = new TreeMap<>(acls);
			for (Entry<String, String> entry : sorted.entrySet()) {
				Button bName = new Button(entry.getKey());
				bName.setWidth("100%");
				addComponent(bName);
				for (String ace : entry.getValue().split(",")) {
					Label lAce = new Label( ace );
					addComponent(lAce);
				}
			}
		}
	}
}
