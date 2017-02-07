package de.mhus.cherry.editor.impl.site;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;

@Component(immediate=true,provide=GuiSpaceService.class)
public class SiteSpaceService extends GuiSpace {

	@Override
	public String getName() {
		return "site";
	}

	@Override
	public String getDisplayName() {
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

}
