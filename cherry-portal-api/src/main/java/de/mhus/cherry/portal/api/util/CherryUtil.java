package de.mhus.cherry.portal.api.util;

import java.io.File;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.ScriptRenderer;
import de.mhus.lib.core.MFile;

public class CherryUtil {

	public static ScriptRenderer getScriptRenderer(CallContext call, File file) {
		return  call.getVirtualHost().getScriptRenderer(MFile.getFileSuffix(file));
	}

}
