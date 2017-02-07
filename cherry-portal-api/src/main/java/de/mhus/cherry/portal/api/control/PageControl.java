package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.NavNode;

public abstract class PageControl extends VerticalLayout {

	
	public abstract void doClean();
	public abstract void doInit(ControlParent controlParent);
	public abstract void doUpdate(NavNode nav);
	
}
