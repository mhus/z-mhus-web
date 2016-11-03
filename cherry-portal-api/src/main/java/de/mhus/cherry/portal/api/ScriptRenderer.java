package de.mhus.cherry.portal.api;

import java.io.File;

public interface ScriptRenderer {

	void doRender(CallContext call, String bundleName, File file) throws Exception;

}
