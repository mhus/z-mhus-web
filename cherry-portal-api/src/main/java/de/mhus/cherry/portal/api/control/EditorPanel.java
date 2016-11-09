package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.VerticalLayout;

import de.mhus.lib.cao.CaoWritableElement;

public abstract class EditorPanel extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	protected CaoWritableElement data;

	public EditorPanel(CaoWritableElement data) {
		this.data = data;
	}
	
	public abstract void initUi();

	public abstract String getTitle();

	public abstract String doSave();
	
}
