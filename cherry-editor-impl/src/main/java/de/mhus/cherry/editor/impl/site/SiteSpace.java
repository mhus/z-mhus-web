package de.mhus.cherry.editor.impl.site;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import de.mhus.lib.core.util.MUri;
import de.mhus.lib.vaadin.desktop.Navigable;

public class SiteSpace extends AbstractComponent implements Navigable {

	private static String TS = "_timestamp";
	private static final long serialVersionUID = 1L;

	@Override
	public String navigateTo(String selection, String filter) {
		if (filter == null) {
			onShowSpace(true);
			return "";
		}
		
		MUri.setParameterValue(filter, TS, String.valueOf(System.currentTimeMillis()));

		UI.getCurrent().getPage().setLocation( filter );
		return "Website " + filter;
	}

	@Override
	public void onShowSpace(boolean firstTime) {
		UI.getCurrent().getPage().setLocation("/");
	}

}
