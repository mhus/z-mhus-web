package de.mhus.cherry.web.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.logging.MLogUtil;

public class CherryWebUtil {
    
    private static CfgLong CFG_CSRF_TIMEOUT = new CfgLong(CherryApi.class, "csrfTimeout", MPeriod.HOUR_IN_MILLISECOUNDS);

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

    public static String createCsrfToken(CallContext context) {
        synchronized (context) {
            @SuppressWarnings("unchecked")
            LinkedList<String> tokens = (LinkedList<String>) context.getSession().get("_csrftokens");
            if (tokens == null) {
                tokens = new LinkedList<String>();
                context.getSession().put("_csrftokens", tokens);
            }
            String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
            tokens.add(token);
            while (tokens.size() > 10)
                tokens.removeFirst();
            return token;
        }
    }
    
    public static boolean isCsrfToken(CallContext context, String token) {
        if (token == null || token.length() == 0 || token.length() > 50) return false;
        String timeStr = MString.afterLastIndex(token, '-');
        if (timeStr == null) return false;
        int time = M.c(timeStr, 0);
        if (time <= 0) return false;
        if (MPeriod.isTimeOut(time, CFG_CSRF_TIMEOUT.value())) return false;

        synchronized (context) {
            @SuppressWarnings("unchecked")
            LinkedList<String> tokens = (LinkedList<String>) context.getSession().get("_csrftokens");
            if (tokens == null)
                return false;
            return tokens.contains(token);
        }
    }

}
