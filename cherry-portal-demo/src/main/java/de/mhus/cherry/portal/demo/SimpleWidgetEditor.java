package de.mhus.cherry.portal.demo;

import com.vaadin.ui.AbstractComponent;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;

@Component(provide = EditorFactory.class, name="cherry_editor_de.mhus.cherry.portal.impl.page.simplewidget")
public class SimpleWidgetEditor implements EditorFactory {

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
	public boolean doPrepareCreateWidget(CaoNode content, String title) {
		return false;
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
