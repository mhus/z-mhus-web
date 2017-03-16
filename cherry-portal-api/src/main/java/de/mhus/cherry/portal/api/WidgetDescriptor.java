package de.mhus.cherry.portal.api;

import de.mhus.cherry.portal.api.control.EditorFactory;

public interface WidgetDescriptor {
	enum TYPE {PAGE,WIDGET, THEME}
	
	TYPE getType();

	EditorFactory getEditorFactory();

	ResourceRenderer getRenderer();

	String getName();
	
}
