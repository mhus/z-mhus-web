package de.mhus.cherry.editor.impl.site;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import de.mhus.cherry.portal.api.control.Navigable;

public class SiteSpace extends AbstractComponent implements Navigable {

	private static final long serialVersionUID = 1L;

	@Override
	public String navigateTo(String selection, String filter) {
		UI.getCurrent().getPage().setLocation( filter );
		return "Website " + filter;
	}

}
