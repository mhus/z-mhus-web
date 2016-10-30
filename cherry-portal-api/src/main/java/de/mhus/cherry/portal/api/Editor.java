package de.mhus.cherry.portal.api;

import com.vaadin.ui.VerticalLayout;

import de.mhus.lib.cao.CaoWritableElement;

public abstract class Editor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	protected CaoWritableElement data;

	public Editor(CaoWritableElement data) {
		this.data = data;
	}
	
	public abstract void initUi();

	public abstract String getTitle();

	public abstract String doSave();
	
}
