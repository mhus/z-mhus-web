package de.mhus.cherry.editor.impl.site;

import java.util.Locale;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;
import de.mhus.lib.vaadin.desktop.HelpContext;

@Component(immediate=true,provide=GuiSpaceService.class)
public class SiteSpaceService extends GuiSpace {

	@Override
	public String getName() {
		return "site";
	}

	@Override
	public String getDisplayName(Locale locale) {
		return "Website";
	}

	@Override
	public AbstractComponent createSpace() {
		return new SiteSpace();
	}

	@Override
	public boolean isHiddenSpace() {
		return false;
	}

	@Override
	public HelpContext createHelpContext(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

}
