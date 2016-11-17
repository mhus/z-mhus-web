package de.mhus.cherry.editor.impl.pages;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.vaadin.easyuploads.UploadField;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.Acl;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MString;
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

		@Override
		public void doUpdate(NavNode nav) {
	        CherryApi api = Sop.getApi(CherryApi.class);
	        CaoNode cur = nav.getCurrent();
			Map<String, Acl> acls = api.getEffectiveAcls(cur);
			TreeMap<String, Acl> sorted = new TreeMap<>(acls);
			for (Entry<String, Acl> entry : sorted.entrySet()) {
				TextField bName = new TextField();
				bName.setValue(entry.getKey());
				bName.setEnabled(false);
//				bName.setCaption("<b>" + entry.getKey() + "</b>");
				bName.setWidth("100%");
				addComponent(bName);

				CaoNode def = entry.getValue().getDefiningNode();
				String txt = null;
				if (def.getId().equals(cur.getId()))
					txt = "From here";
				else
					txt = MString.truncateNiceLeft(def.getPath(), 40);
				Label src = new Label(txt);
				addComponent(src);
				for (String ace : entry.getValue().getAces()) {
					Label lAce = new Label( ace );
					addComponent(lAce);
				}
			}
		}
	}
}
