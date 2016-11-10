package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.AbstractComponent;

import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;

public interface EditorFactory extends Named {

	enum TYPE {PAGE, WIDGET}
	EditorPanel createEditor(CaoWritableElement data);
	AbstractComponent createPreview(CaoNode res);
	LayoutPanel createLayoutPanel(CaoNode res);
	
	boolean doPrepareCreateWidget(CaoNode content, String title);
	boolean doDeleteWidget(CaoNode res);
	
	TYPE getType();
	
	
}
