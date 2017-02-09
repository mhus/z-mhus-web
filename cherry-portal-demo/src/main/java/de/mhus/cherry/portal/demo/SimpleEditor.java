package de.mhus.cherry.portal.demo;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;

import de.mhus.cherry.editor.impl.forms.CherryRichTextArea;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.errors.MException;

public class SimpleEditor extends EditorPanel {

	private static Log log = Log.getLog(SimpleEditor.class);
	private static final long serialVersionUID = 1L;
	private TextField title;
	private Component text;
	private CheckBox hidden;
	private CherryRichTextArea textUi;

	public SimpleEditor(CaoWritableElement data) {
		super(data);
	}

	@Override
	public void initUi() {
		
		title = new TextField("Title");
		title.setValue(data.getString("title", ""));
		title.setWidth("100%");
		addComponent(title);
		
		textUi = new CherryRichTextArea();
		text = textUi.createEditor();
		textUi.setComponentEditor(text);
		
		try {
			textUi.setValue(data.getString("text", ""));
		} catch (MException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text.setSizeFull();
		addComponent(text);

	}
	
	@Override
	public String getTitle() {
		return "Simple " + data.getString("title", "");
	}

	@Override
	public String doSave() {
		data.setString("title", title.getValue());
		try {
			data.setString("text", String.valueOf(textUi.getValue() ) );
			data.getUpdateAction().doExecute(null);
		} catch (MException e) {
			log.e(e);
			return e.toString();
		}
		return null;
	}

}
