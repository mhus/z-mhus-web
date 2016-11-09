package de.mhus.cherry.editor.impl.editor;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.control.EditorControl;
import de.mhus.cherry.portal.api.control.EditorControlFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;

@Component
public class EditorAccessControlFactory implements EditorControlFactory {

	@Override
	public EditorControl createEditorControl(CaoNode res, EditorFactory editorFactory) {
		return new Control(res);
	}

	@Override
	public String getName() {
		return "Access";
	}

	private static class Control extends EditorControl {

		public Control(CaoNode res) {
			
			
		}
		
	}
}
