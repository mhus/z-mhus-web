package de.mhus.cherry.portal.api;

import de.mhus.lib.servlet.RequestWrapper;
import de.mhus.osgi.sop.api.aaa.AaaContext;

public interface LoginHandler {

	AaaContext doLogin(RequestWrapper request);

}
