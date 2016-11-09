package de.mhus.cherry.portal.api.control;

import de.mhus.lib.cao.CaoNode;

public interface EditorControlFactory {

	EditorControl createEditorControl(CaoNode res);

	String getName();
	
}
