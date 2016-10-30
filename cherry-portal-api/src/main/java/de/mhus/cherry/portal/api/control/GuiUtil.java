package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.UI;

public class GuiUtil {

	public static GuiApi getApi() {
		return (GuiApi) UI.getCurrent();
	}
}
