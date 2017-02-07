package de.mhus.cherry.editor.impl.pages;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.vaadin.desktop.GuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;

@Component(immediate=true,provide=GuiSpaceService.class)
public class PagesSpaceService extends GuiSpace {

	@Override
	public String getName() {
		return "pages";
	}

	@Override
	public String getDisplayName() {
		return "Pages";
	}

	@Override
	public AbstractComponent createSpace() {
		return new PagesSpace();
	}

}
