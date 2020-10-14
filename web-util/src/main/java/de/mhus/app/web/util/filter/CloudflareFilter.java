/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import de.mhus.app.web.api.CallContext;
import de.mhus.app.web.api.InternalCallContext;
import de.mhus.app.web.api.VirtualHost;
import de.mhus.app.web.api.WebFilter;
import de.mhus.app.web.util.CherryWebUtil;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPassword;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.cfg.CfgString;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.net.Subnet;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

// https://en.wikipedia.org/wiki/Basic_access_authentication
public class CloudflareFilter extends MLog implements WebFilter {

    public static String NAME = "base_auth_filter";
    private static Subnet[] cloudflareNetworks;
    // https://www.cloudflare.com/ips-v4
    private static CfgString CFG_IPS =
            new CfgString(
                    CloudflareFilter.class,
                    "ips",
                    "103.21.244.0/22,103.22.200.0/22,103.31.4.0/22,104.16.0.0/12,"
                            + "108.162.192.0/18,131.0.72.0/22,141.101.64.0/18,162.158.0.0/15,"
                            + "172.64.0.0/13,173.245.48.0/20,188.114.96.0/20,190.93.240.0/20,"
                            + "197.234.240.0/22,198.41.128.0/17,"
                            + "2400:cb00::/32,2405:b500::/32,2606:4700::/32,2803:f800::/32,2c0f:f248::/32,2a06:98c0::/29") {

                @Override
                protected void onPreUpdate(String newValue) {
                    cloudflareNetworks = null;
                }
            };

    @Override
    public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException {
        vHost.getProperties().put(NAME + instance, new Config(vHost, config));
    }

    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
        Config config = (Config) call.getVirtualHost().getProperties().get(NAME + instance);
        String account = "";
        if (config == null) {
            send401(call, config);
            return false;
        }
        if (isCloudflare(call.getHttpRequest().getRemoteAddr())) {
            String remoteIP = getRemoteIp(call.getHttpRequest());
            call.setRemoteIp(remoteIP);

            if (!config.public_) {
                account = doAuth(call, config);
                if (account == null) {
                    trace(call, account, config, remoteIP);
                    send401(call, config);
                    return false;
                }
            }
            trace(call, account, config, remoteIP);

            return true;
        } else {
            String remoteIP = call.getHttpRequest().getRemoteAddr();
            call.setAttribute(CallContext.REQUEST_REMOTE_IP, remoteIP);

            account = doAuth(call, config);
            trace(call, account, config, remoteIP);
            if (account == null) {
                send401(call, config);
                return false;
            }
            return true;
        }
    }

    public static String getRemoteIp(HttpServletRequest request) {
        return request.getHeader("CF-Connecting-IP");
    }

    private String doAuth(InternalCallContext call, Config config) throws MException {
        String auth = call.getHttpRequest().getHeader("Authorization");
        if (auth == null) {
            return null;
        }
        if (!auth.toUpperCase().startsWith("BASIC ")) {
            return null; // we only do BASIC
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
            if (MPassword.equals(accPass, pass)) return account;
            else log().d("password not accepted", account);
        } else log().d("user not found", account);

        return null;
    }

    private void trace(InternalCallContext call, String account, Config config, String remoteIP) {
        if (call.getVirtualHost().isTraceAccess())
            log().d(
                            "access",
                            call.getVirtualHost().getName(),
                            account,
                            remoteIP,
                            call.getHttpMethod(),
                            call.getHttpPath());
    }

    public static boolean isCloudflare(String ip) {
        try {
            InetAddress remoteIP = InetAddress.getByName(ip);
            for (Subnet net : getCloudflareNetworks())
                if (net != null && net.isInNet(remoteIP)) return true;
        } catch (UnknownHostException e) {
            MLogUtil.log().w(e);
        }
        return false;
    }

    private static Subnet[] getCloudflareNetworks() {
        if (cloudflareNetworks == null) {

            String[] ips = null;
            // TODO loading from web is disabled ...
            //	           String url = config.getString("url","none"); //
            // "https://www.cloudflare.com/ips-v4"
            //	            IConfig ipNode = config.getNode("ips");
            //	            if (ipNode != null) {
            //	                ips = MConfig.toStringArray(ipNode.getNodes(), "value");
            //	            } else
            //	            if (url.startsWith("file:")) {
            //	                File f = new File(url.substring(5));
            //	                try {
            //	                    ips = MFile.readLines(f, true).toArray(new String[0]);
            //	                } catch (Exception e) {
            //	                    log().e(e);
            //	                }
            //	            } else
            //	            if (!"none".equals(url))
            //	            {
            //	                try {
            //	                    HttpGet get = new HttpGet(url);
            //	                    HttpResponse res = MHttp.getSharedClient().execute(get);
            //	                    if (res.getStatusLine().getStatusCode() != 200) {
            //	                        log().e("Failed to get IPs from
            // cloudflare",res.getStatusLine().getReasonPhrase(),url);
            //	                    } else {
            //	                        InputStream is = res.getEntity().getContent();
            //	                        ips = MFile.readLines(is, true).toArray(new String[0]);
            //	                    }
            //	                } catch (Exception e) {
            //	                    log().e(e);
            //	                }
            //	            }

            ips = CFG_IPS.value().split(",");

            cloudflareNetworks = new Subnet[ips.length];
            for (int i = 0; i < ips.length; i++)
                try {
                    cloudflareNetworks[i] = Subnet.createInstance(ips[i]);
                } catch (UnknownHostException e) {
                    MLogUtil.log().e(ips[i], e);
                }
        }
        return cloudflareNetworks;
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
            throw new MException(e);
        }
    }

    @Override
    public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {}

    private class Config {

        private String realm;
        private String message;
        private HashMap<String, String> accounts = new HashMap<>();
        private boolean public_;

        public Config(VirtualHost vHost, IConfig config) {
            message = config.getString("message", "Access denied");
            realm = config.getString("realm", "Access");
            for (IConfig node : config.getObjectList("accounts"))
                try {
                    accounts.put(node.getString("user"), node.getString("pass"));
                } catch (MException e) {
                    log().e(e);
                }
            String accountsFile = config.getString("accountsFile", null);
            if (MString.isSet(accountsFile))
                CherryWebUtil.loadAccounts(vHost.findFile(accountsFile), accounts);
            public_ = config.getBoolean("public", true);
        }
    }
}
