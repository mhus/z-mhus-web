package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.VerticalLayout;

public abstract class LayoutPanel extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	public abstract void doReload();
	
}
