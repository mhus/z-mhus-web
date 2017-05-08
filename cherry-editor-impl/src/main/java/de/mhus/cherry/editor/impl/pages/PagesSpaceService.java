package de.mhus.cherry.editor.impl.pages;

import java.util.Locale;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;
import de.mhus.lib.vaadin.desktop.HelpContext;
import de.mhus.lib.vaadin.desktop.NlsHelpContext;

@Component(immediate=true,provide=GuiSpaceService.class)
public class PagesSpaceService extends GuiSpace {

	@Override
	public String getName() {
		return "pages";
	}

	@Override
	public String getDisplayName(Locale locale) {
		return "Pages";
	}

	@Override
	public AbstractComponent createSpace() {
		return new PagesSpace();
	}

	@Override
	public HelpContext createHelpContext(Locale locale) {
		return new NlsHelpContext(this,locale);
	}

}
