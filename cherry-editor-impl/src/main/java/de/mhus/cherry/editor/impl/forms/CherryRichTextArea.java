package de.mhus.cherry.editor.impl.forms;

import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.ComponentAdapter;
import de.mhus.lib.form.ComponentDefinition;
import de.mhus.lib.form.UiComponent;
import de.mhus.lib.vaadin.form.UiTextArea;
import de.mhus.lib.vaadin.form.UiVaadin;

public class CherryRichTextArea extends UiVaadin {

	@Override
	public void setValue(Object value) throws MException {
		((CKEditorTextField)getComponentEditor()).setValue(MCast.toString(value));
	}

	@Override
	public Component createEditor() {
		
        CKEditorConfig config = new CKEditorConfig();
        config.useCompactTags();
        config.disableElementsPath();
        config.setResizeDir(CKEditorConfig.RESIZE_DIR.BOTH);
        config.disableSpellChecker();
        config.setWidth("100%");
        config.setFilebrowserImageBrowseLinkUrl("/");
        config.setFilebrowserImageBrowseUrl("/vaadinaddons#!imagebrowser");
        config.setFilebrowserImageUploadUrl("/");
        config.setFilebrowserUploadUrl("/");
        config.setFilebrowserLinkBrowseUrl("/vaadinaddons#!linkbrowser");
        final CKEditorTextField ckEditorTextField = new CKEditorTextField(config);
        
        ckEditorTextField.setValue("");
//        ckEditorTextField.addValueChangeListener(new Property.ValueChangeListener() {
//            public void valueChange(ValueChangeEvent event) {
//                Notification.show("CKEditor v" + ckEditorTextField.getVersion() + " - contents: " + event.getProperty().getValue().toString());
//            }
//        });    
		
		return ckEditorTextField;
	}

	@Override
	public Object getValue() throws MException {
		return ((CKEditorTextField)getComponentEditor()).getValue();
	}

	public static class Adapter implements ComponentAdapter {

		@Override
		public UiComponent createAdapter(IConfig config) {
			return new CherryRichTextArea();
		}

		@Override
		public ComponentDefinition getDefinition() {
			return null;
		}
		
	}

}
