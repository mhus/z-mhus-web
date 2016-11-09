package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.AbstractComponent;

import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;

public interface EditorControlFactory extends Named {

	@Override
	String getName();
	
	EditorControl createEditorControl(CaoNode res, EditorFactory editorFactory);
	
}
