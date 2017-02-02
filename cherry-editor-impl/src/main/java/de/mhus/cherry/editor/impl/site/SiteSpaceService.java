package de.mhus.cherry.editor.impl.site;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.security.AccessControl;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;

@Component(immediate=true,provide=GuiSpaceService.class)
public class SiteSpaceService implements GuiSpaceService {

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
	public boolean hasAccess(AccessControl control) {
		return true;
	}

	@Override
	public void createMenu(final AbstractComponent space, MenuItem[] menu) {
	}

	@Override
	public boolean isHiddenSpace() {
		return true;
	}

	@Override
	public AbstractComponent createTile() {
		return null;
	}

	@Override
	public int getTileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
