package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.AbstractComponent;

import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.util.MNlsProvider;
import de.mhus.lib.core.util.Nls;

public interface EditorFactory extends Named, Nls {

	enum TYPE {PAGE, WIDGET}
	EditorPanel createEditor(CaoWritableElement data);
	AbstractComponent createPreview(CaoNode res);
	LayoutPanel createLayoutPanel(CaoNode res);
	
	boolean doPrepareCreateWidget(CaoNode content, String title);
	boolean doDeleteWidget(CaoNode res);
	
	TYPE getType();
	String getCaption();
	String getIdent();
	
	
}
