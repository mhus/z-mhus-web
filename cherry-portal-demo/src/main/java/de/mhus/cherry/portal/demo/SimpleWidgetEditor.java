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
	public CaoNode createPage(CaoNode parentNav, String title) {
		return null;
	}

	@Override
	public boolean deletePage(CaoNode nav) {
		return false;
	}

	@Override
	public CaoNode createWidget(CaoNode parent, String title) {
		return null;
	}

	@Override
	public boolean deleteWidget(CaoNode res) {
		return false;
	}

	@Override
	public boolean isPage() {
		return false;
	}

	@Override
	public boolean isWidget() {
		return true;
	}

	@Override
	public LayoutPanel createLayoutPanel(CaoNode res) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "simpleWidget";
	}

}
