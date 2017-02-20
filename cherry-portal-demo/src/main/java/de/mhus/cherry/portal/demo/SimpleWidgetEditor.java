package de.mhus.cherry.portal.demo;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.AbstractEditorFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;

@Component(provide = EditorFactory.class, name="cherry_editor_" + SimpleWidgetEditor.NAME)
public class SimpleWidgetEditor extends AbstractEditorFactory implements EditorFactory {

	public static final String NAME = "de.mhus.cherry.portal.impl.page.simplewidget";
	
	@Override
	public EditorPanel createEditor(CaoWritableElement data) {
		return new SimpleEditor(data);
	}

	@Override
	public AbstractComponent createPreview(CaoNode res) {
		return null;
	}

	@Override
	public LayoutPanel createLayoutPanel(CaoNode res) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "SimpleWidget";
	}

	@Override
	public boolean doPrepareCreatedWidget(CaoNode content) {
		return true;
	}

	@Override
	public boolean doDeleteWidget(CaoNode res) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TYPE getType() {
		return TYPE.WIDGET;
	}

}
