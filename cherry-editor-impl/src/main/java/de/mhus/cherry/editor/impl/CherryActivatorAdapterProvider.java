package de.mhus.cherry.editor.impl;

import de.mhus.cherry.editor.impl.forms.CherryRichTextArea;
import de.mhus.lib.core.activator.DefaultActivator;
import de.mhus.lib.vaadin.form.DefaultAdapterProvider;

public class CherryActivatorAdapterProvider extends DefaultAdapterProvider {

	public CherryActivatorAdapterProvider() {
		super();
		((DefaultActivator)activator).addMap("richtext", CherryRichTextArea.Adapter.class);
	}
}
