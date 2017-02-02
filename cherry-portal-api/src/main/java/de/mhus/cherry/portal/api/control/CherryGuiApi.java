package de.mhus.cherry.portal.api.control;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.vaadin.desktop.GuiApi;

public interface CherryGuiApi extends GuiApi {

	boolean openSpace(String spaceId, String subSpace, String search);
	
	void navigateToEditor(CaoNode content);

	void navigateBack();
		
}
