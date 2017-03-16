package de.mhus.cherry.portal.demo;

import de.mhus.cherry.portal.api.ResourceRenderer;
import de.mhus.cherry.portal.api.WidgetDescriptor;
import de.mhus.cherry.portal.api.control.EditorFactory;

public class SimpleWidgetDescriptor implements WidgetDescriptor {

	private TYPE type;
	private EditorFactory editorFactory;
	private ResourceRenderer rednerer;
	private String name;
	
	public SimpleWidgetDescriptor(String name, TYPE type, ResourceRenderer rednerer, EditorFactory editorFactory) {
		this.name = name;
		this.type = type;
		this.rednerer = rednerer;
		this.editorFactory = editorFactory;
	}
	
	@Override
	public TYPE getType() {
		return type;
	}

	@Override
	public EditorFactory getEditorFactory() {
		return editorFactory;
	}

	@Override
	public ResourceRenderer getRenderer() {
		return rednerer;
	}

	@Override
	public String getName() {
		return name;
	}

}
