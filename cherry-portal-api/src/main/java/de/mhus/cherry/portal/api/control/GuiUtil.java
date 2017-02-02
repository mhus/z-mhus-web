package de.mhus.cherry.portal.api.control;

import com.vaadin.ui.UI;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.osgi.sop.api.Sop;

public class GuiUtil {

	public static CherryGuiApi getApi() {
		return (CherryGuiApi) UI.getCurrent();
	}

	public static VirtualHost getVirtualHost() {
		String host = getApi().getHost();
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost(host);
		return vHost;
	}
}
