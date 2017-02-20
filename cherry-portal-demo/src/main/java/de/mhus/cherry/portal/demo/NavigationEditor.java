package de.mhus.cherry.portal.demo;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;

import de.mhus.cherry.portal.api.AbstractEditorFactory;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.errors.MException;

public class NavigationEditor extends AbstractEditorFactory {

	@Override
	public EditorPanel createEditor(CaoWritableElement data) {
		return new Editor(data);
	}

	@Override
	public AbstractComponent createPreview(CaoNode res) {
		return null;
	}

	@Override
	public LayoutPanel createLayoutPanel(CaoNode res) {
		return null;
	}

	@Override
	public boolean doPrepareCreatedWidget(CaoNode content) {
		return false;
	}

	@Override
	public boolean doDeleteWidget(CaoNode res) {
		return false;
	}

	@Override
	public TYPE getType() {
		return TYPE.PAGE;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	private class Editor extends EditorPanel {

		private static final long serialVersionUID = 1L;
		private TextField title;

		public Editor(CaoWritableElement data) {
			super(data);
		}

		@Override
		public void initUi() {
			title = new TextField("Title");
			title.setValue(data.getString("title", ""));
			title.setWidth("100%");
			addComponent(title);

		}

		@Override
		public String getTitle() {
			return "Navigation";
		}

		@Override
		public String doSave() {
			try {
				data.setString("title", title.getValue());
				data.getUpdateAction().doExecute(null);
			} catch (MException e) {
				log().e(e);
				return e.toString();
			}
			return null;
		}
		
	}
}
