/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.CherryApi;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.util.webspace.CallConfigProvider;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.crypt.MRandom;
import de.mhus.lib.core.logging.MLogUtil;

public class CherryWebUtil {

    private static CfgLong CFG_CSRF_TIMEOUT =
            new CfgLong(CherryApi.class, "csrfTimeout", MPeriod.HOUR_IN_MILLISECOUNDS);

    public static void loadAccounts(File file, HashMap<String, String> accounts) {
        try {
            for (String line : MFile.readLines(file, true)) {
                line = line.trim();
                if (line.startsWith("#")) continue;
                int pos = line.indexOf(':');
                if (pos > 0) {
                    accounts.put(line.substring(0, pos), line.substring(pos + 1));
                }
            }
        } catch (IOException e) {
            MLogUtil.log().e(file, e);
        }
    }

    public static String createCsrfToken(CallContext context) {
        synchronized (context) {
            @SuppressWarnings("unchecked")
            LinkedList<String> tokens =
                    (LinkedList<String>) context.getSession().get("_csrftokens");
            if (tokens == null) {
                tokens = new LinkedList<String>();
                context.getSession().put("_csrftokens", tokens);
            }
            StringBuilder token = new StringBuilder();
            MRandom rnd = M.l(MRandom.class);
            for (int i = 0; i < 40; i++) token.append(rnd.getChar());
            token.append('-').append(System.currentTimeMillis());
            String tokenStr = token.toString();
            tokens.add(tokenStr);
            while (tokens.size() > 10) tokens.removeFirst();
            return tokenStr;
        }
    }

    public static boolean isCsrfToken(CallContext context, String token) {
        if (token == null || token.length() == 0 || token.length() > 60) return false;
        String timeStr = MString.afterLastIndex(token, '-');
        if (timeStr == null) return false;
        long time = MCast.tolong(timeStr, 0);
        if (time <= 0) return false;
        if (MPeriod.isTimeOut(time, CFG_CSRF_TIMEOUT.value())) return false;

        synchronized (context) {
            @SuppressWarnings("unchecked")
            LinkedList<String> tokens =
                    (LinkedList<String>) context.getSession().get("_csrftokens");
            if (tokens == null) return false;
            return tokens.contains(token);
        }
    }

    public static IReadProperties findConfig(InternalCallContext call) {
        VirtualHost vHost = call.getVirtualHost();
        if (vHost == null) return null;
        if (!(vHost instanceof CallConfigProvider)) return null;
        return ((CallConfigProvider) vHost).findConfig(call);
    }
}
