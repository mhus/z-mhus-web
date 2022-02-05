/**
 * Copyright (C) 2015 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.web.util.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;

import de.mhus.app.web.api.InternalCallContext;
import de.mhus.app.web.api.VirtualHost;
import de.mhus.app.web.api.WebFilter;
import de.mhus.app.web.util.CherryWebUtil;
import de.mhus.lib.basics.RC;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

// https://en.wikipedia.org/wiki/Basic_access_authentication
public class BaseAuthFilter extends MLog implements WebFilter {

    public static String NAME = "base_auth_filter";

    @Override
    public void doInitialize(UUID instance, VirtualHost vHost, INode config) throws MException {
        vHost.getProperties().put(NAME + instance, new Config(vHost, config));
    }

    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
        Config config = (Config) call.getVirtualHost().getProperties().get(NAME + instance);
        if (config == null) {
            send401(call, config);
            return false;
        }
        String path = call.getHttpPath();
        if (config.included != null && !config.included.matcher(path).matches()) return true;
        if (config.excluded != null && config.excluded.matcher(path).matches()) return true;

        String auth = call.getHttpRequest().getHeader("Authorization");
        if (auth == null) {
            send401(call, config);
            return false;
        }
        if (!auth.toUpperCase().startsWith("BASIC ")) {
            send401(call, config);
            return false; // we only do BASIC
        }
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        // Decode it, using any base 64 decoder
        String userpassDecoded = new String(Base64.decode(userpassEncoded));
        // Check our user list to see if that user and password are "allowed"
        String[] parts = userpassDecoded.split(":", 2);
        String account = null;
        String pass = null;
        if (parts.length > 0) account = MUri.decode(parts[0]);
        if (parts.length > 1) pass = MUri.decode(parts[1]);
        String accPass = config.accounts.get(account);
        if (accPass != null) {
            if (MPassword.equals(accPass, pass)) {
                if (call.getVirtualHost().isTraceAccess())
                    log().d(
                                    "access",
                                    call.getVirtualHost().getName(),
                                    account,
                                    call.getHttpMethod(),
                                    call.getHttpPath());
                return true;
            } else {
                log().d("password not accepted", account, call.getHttpRequest().getRemoteAddr());
            }
        } else log().d("user not found", account, call.getHttpRequest().getRemoteAddr());

        send401(call, config);
        return false;
    }

    private void send401(InternalCallContext call, Config config) throws MException {
        try {
            call.getHttpResponse().setStatus(401);
            call.getHttpResponse()
                    .setHeader(
                            "WWW-Authenticate",
                            "BASIC realm=\"" + config.realm + "\", charset=\"UTF-8\"");
            call.getHttpResponse().setContentType("text/html");
            ServletOutputStream os = call.getHttpResponse().getOutputStream();
            os.write(config.message.getBytes()); // utf-8?
            os.flush();
        } catch (IOException e) {
            throw new MException(RC.ERROR, e);
        }
    }

    @Override
    public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {}

    private class Config {

        private String realm;
        private Pattern included;
        private Pattern excluded;
        private String message;
        private HashMap<String, String> accounts = new HashMap<>();

        public Config(VirtualHost vHost, INode config) {
            String includedStr = config.getString("included", null);
            if (includedStr != null) included = Pattern.compile(includedStr);
            String excludedStr = config.getString("excluded", null);
            if (excludedStr != null) excluded = Pattern.compile(excludedStr);
            message = config.getString("message", "Access denied");
            realm = config.getString("realm", "Access");
            for (INode node : config.getObjectList("accounts"))
                try {
                    accounts.put(node.getString("user"), node.getString("pass"));
                } catch (MException e) {
                    log().e(e);
                }
            String accountsFile = config.getString("accountsFile", null);
            if (MString.isSet(accountsFile))
                CherryWebUtil.loadAccounts(vHost.findFile(accountsFile), accounts);
        }
    }
}
