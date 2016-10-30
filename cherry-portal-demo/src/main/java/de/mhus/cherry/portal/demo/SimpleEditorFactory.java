package de.mhus.cherry.portal.demo;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.Editor;
import de.mhus.cherry.portal.api.EditorFactory;
import de.mhus.lib.cao.CaoWritableElement;

@Component(provide = EditorFactory.class, name="cherry_editor_de.mhus.cherry.portal.demo.simpleeditorfactory")
public class SimpleEditorFactory implements EditorFactory {

	@Override
	public Editor createEditor(CaoWritableElement data) {
		return new SimpleEditor(data);
	}

}
