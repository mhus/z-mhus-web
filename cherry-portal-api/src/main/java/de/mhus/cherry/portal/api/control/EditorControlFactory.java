package de.mhus.cherry.portal.api.control;

import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;

public interface EditorControlFactory extends Named {

	EditorControl createEditorControl(CaoNode res);

	@Override
	String getName();
	
}
