package de.mhus.cherry.portal.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mhus.lib.cao.CaoNode;

public interface ResourceRenderer {

	void doRender(CallContext call) throws Exception;
}
