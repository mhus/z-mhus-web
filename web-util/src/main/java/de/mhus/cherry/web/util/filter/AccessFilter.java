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
package de.mhus.cherry.web.util.filter;

import java.util.Set;
import java.util.UUID;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.cherry.web.util.CherryWebUtil;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.errors.MException;

public class AccessFilter extends MLog implements WebFilter {

    private static final String CALL_START = "filter_AccessFilter_start";
    private static final String SESSION_USER_OBJECT =
            "de.mhus.cherry.web.util.filter.AccessFilter.UserInformation";
    private static final String SESSION_USER_NAME = "username";
    private static final String SESSION_GROUP_PREFIX = "GROUP_";
    private static final String SESSION_USER_DISPLAY_NAME = "userdisplayname";
    private String[] staticContent;
    private boolean defaultPublicAccess = true;

    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {

        String path = call.getHttpPath();
        if (isStaticContentPath(path)) {
            if (call.getVirtualHost().prepareHead(call, "_static_content", false) == null)
                call.getHttpResponse().setHeader("Cache-Control", "max-age=290304000, public");
        } else {
            if (call.getVirtualHost().prepareHead(call, "_dynamic_content", false) == null) {
                call.getHttpResponse()
                        .setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                call.getHttpResponse().setHeader("Pragma", "no-cache");
                call.getHttpResponse().setIntHeader("Expires", 0);
            }
        }
        // security filter
        String userName = "public";
        UserInformation user = null;
        if (call.getSession() != null) {
            user = (UserInformation) call.getSession().get(SESSION_USER_OBJECT);
            if (user != null) userName = user.getUserName();
        }

        IReadProperties config = CherryWebUtil.findConfig(call);
        if (config != null) {
            boolean public_ = config.getBoolean("public", defaultPublicAccess);
            if (!public_) {
                if (user == null) {
                    call.getVirtualHost().sendError(call, 404, null);
                    return false;
                }
                if (!hasAccess(call, user, config)) {
                    call.getVirtualHost().sendError(call, 404, null);
                    return false;
                }
            }
        }
        long start = System.currentTimeMillis();
        call.setAttribute(CALL_START, start);
        log().d(
                        "access",
                        userName,
                        call.getVirtualHost().getName(),
                        call.getRemoteIp(),
                        call.getHttpRequest().getRemoteAddr(),
                        call.getHttpHost(),
                        call.getHttpMethod(),
                        call.getHttpPath());

        return true;
    }

    /**
     * Overwrite to grant access. The default is using access_groups and access_users from file
     * configuration.
     *
     * @param call
     * @param user
     * @param config
     * @return
     */
    protected boolean hasAccess(
            InternalCallContext call, UserInformation user, IReadProperties config) {
        String groups = config.getString("access_groups", null);
        String users = config.getString("access_users", null);
        // TODO Performance problem, do not use in production, e.g. use a cache
        if (groups != null) {
            Set<String> userGroups = user.getGroups();
            for (String g : groups.split(",")) if (userGroups.contains(g)) return true;
        }
        if (users != null) {
            for (String u : users.split(",")) if (user.getUserName().equals(u)) return true;
        }
        return false;
    }

    protected boolean isStaticContentPath(String path) {
        if (staticContent == null) return false;
        for (String p : staticContent) {
            if (path.matches(p)) return true;
        }
        return false;
    }

    @Override
    public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
        Long start = (Long) call.getAttribute(CALL_START);
        if (start != null) {
            long duration = System.currentTimeMillis() - start;
            String durationStr = MPeriod.getIntervalAsString(duration);
            log().d(
                            "duration",
                            durationStr,
                            duration,
                            call.getHttpHost(),
                            call.getHttpMethod(),
                            call.getHttpPath());
        }
    }

    @Override
    public void doInitialize(UUID instance, VirtualHost vhost, IConfig config) throws MException {

        if (config != null) {
            if (config.isProperty("static")) {
                staticContent = MConfig.toStringArray(config.getNode("static").getNodes(), "value");
            }
            defaultPublicAccess = config.getBoolean("defaultPublicAccess", defaultPublicAccess);
        }
    }

    public static UserInformation getUserInformation(CallContext call) {
        return (UserInformation) call.getSession().get(SESSION_USER_OBJECT);
    }

    public static void doLogout(CallContext call) {
        call.getSession().remove(SESSION_USER_OBJECT);
        call.getSession().pub().remove(SESSION_USER_NAME);
        call.getSession().pub().remove(SESSION_USER_DISPLAY_NAME);
        call.getSession().pub().keys().removeIf(n -> n.startsWith(SESSION_GROUP_PREFIX));
        call.getHttpRequest().changeSessionId();
    }

    public static void doLogin(CallContext call, UserInformation user) {
        call.getSession().put(SESSION_USER_OBJECT, user);
        IProperties p = call.getSession().pub();
        p.setString(SESSION_USER_NAME, user.getUserName());
        p.setString(SESSION_USER_DISPLAY_NAME, user.getDisplayName());
        for (String g : user.getGroups())
            p.setBoolean(SESSION_GROUP_PREFIX + g.toUpperCase(), true);
    }
}
