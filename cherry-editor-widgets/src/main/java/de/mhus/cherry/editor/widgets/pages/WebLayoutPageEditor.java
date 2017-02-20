package de.mhus.cherry.editor.widgets.pages;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.AbstractEditorFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.errors.MException;

@Component(provide = EditorFactory.class, name="cherry_editor_" + WebLayoutPageEditor.NAME)
public class WebLayoutPageEditor extends AbstractEditorFactory implements EditorFactory  {

	public final static String NAME = "de.mhus.cherry.editor.widgets.pages.weblayoutpageeditor";

	class Editor extends EditorPanel {
	
		private static final long serialVersionUID = 1L;
		private TextField title;
		private CheckBox hidden;
	
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
			return "Page " + data.getString("title", "");
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

	@Override
	public String getName() {
		return "Layout Page";
	}

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

}
