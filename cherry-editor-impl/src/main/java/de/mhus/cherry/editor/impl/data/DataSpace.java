package de.mhus.cherry.editor.impl.data;

import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.lib.vaadin.desktop.Navigable;

public class DataSpace extends VerticalLayout implements Navigable, GuiLifecycle, ControlParent {

	private static final long serialVersionUID = 1L;

	@Override
	public void doInitialize() {
		
	}

	@Override
	public void doRefreshNode(CaoNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String navigateTo(String selection, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onShowSpace(boolean firstTime) {
		// TODO Auto-generated method stub
		
	}

}
