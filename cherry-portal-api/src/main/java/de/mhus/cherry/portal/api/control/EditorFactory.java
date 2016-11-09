package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.AbstractComponent;

import de.mhus.lib.basics.Named;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;

public interface EditorFactory extends Named {

	EditorPanel createEditor(CaoWritableElement data);
	AbstractComponent createPreview(CaoNode res);
	LayoutPanel createLayoutPanel(CaoNode res);
	
	CaoNode createPage(CaoNode parentNav, String title);
	boolean deletePage(CaoNode nav);
	
	CaoNode createWidget(CaoNode parent, String title);
	boolean deleteWidget(CaoNode res);
	
	boolean isPage();
	boolean isWidget();

	
	
}
