package de.mhus.cherry.web.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.MLogUtil;

public class CherryWebUtil {

	public static void loadAccounts(File file, HashMap<String, String> accounts) {
		try {
			for ( String line : MFile.readLines(file, true)) {
				line = line.trim();
				if (line.startsWith("#")) continue;
				int pos = line.indexOf(':');
				if (pos > 0) {
					accounts.put(line.substring(0,pos), line.substring(pos+1));
				}
			}
		} catch (IOException e) {
			MLogUtil.log().e(file,e);
		}
	}

}
