package de.mhus.cherry.portal.api;

import de.mhus.lib.cao.CaoWritableElement;

public interface EditorFactory {

	public Editor createEditor(CaoWritableElement data);
	
}
