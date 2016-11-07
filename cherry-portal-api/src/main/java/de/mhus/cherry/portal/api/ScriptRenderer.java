package de.mhus.cherry.portal.api;

import java.io.File;

import org.osgi.framework.Bundle;

public interface ScriptRenderer {

	void doRender(CallContext call, Bundle bundle, File file) throws Exception;

}
