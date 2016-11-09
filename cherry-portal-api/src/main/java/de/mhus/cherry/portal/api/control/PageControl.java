package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.VerticalLayout;

import de.mhus.lib.cao.CaoNode;

public abstract class PageControl extends VerticalLayout {

	
	public abstract void doClean();
	public abstract void doInit(ControlParent controlParent);
	public abstract void doUpdate(CaoNode nav, CaoNode res);
	
}
