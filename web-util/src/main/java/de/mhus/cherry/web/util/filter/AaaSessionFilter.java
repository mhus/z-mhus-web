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
package de.mhus.cherry.web.util.filter;

import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.cherry.web.api.InternalCallContext;
import de.mhus.cherry.web.api.VirtualHost;
import de.mhus.cherry.web.api.WebFilter;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.shiro.AccessApi;
import de.mhus.lib.core.util.Base64;
import de.mhus.lib.errors.MException;

public class AaaSessionFilter extends MLog implements WebFilter {

    public static final String SESSION_PARAMETER_NAME = "__sop_user_ticket";
    public static final String CONTEXT_PARAMETER_AAA_CONTEXT = "__sop_aaa_context";

    @SuppressWarnings("unused")
    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
        try {
            HttpServletRequest req = call.getHttpRequest();

            String authHeader = req.getHeader("authorization");
            if (authHeader != null) {
                String encodedValue = authHeader.split(" ")[1];
                String decodedValue = Base64.decodeToString(encodedValue);
                // log().d(decodedValue);
                // TODO not finished yet !!!!
            }

            AccessApi aaa = M.l(AccessApi.class);

            if (call.isSession()) {
                String userTicket = call.getSession().getString(SESSION_PARAMETER_NAME, null);
                if (userTicket == null) return true; // guest?

                Locale locale = req.getLocale();

                // XXX                AaaContext userContext = aaa.process(userTicket, locale);
                //                call.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, userContext);
            }
        } catch (Throwable t) {
            throw new MException(t);
        }
        return true;
    }

    @Override
    public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
        //        AaaContext userContext = (AaaContext)
        // call.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
        //        if (userContext == null) return;
        //
        //        AccessApi aaa = M.l(AccessApi.class);
        //        aaa.release(userContext);
    }

    public static boolean isLoggedIn(CallContext context) {
        //        AaaContext userContext = (AaaContext)
        // context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
        //        return userContext != null;
        return false;
    }

    public static void login(CallContext context, String user, String pass) throws MException {

        //        AaaContext userContext = (AaaContext)
        // context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
        //        if (userContext != null)
        //            throw new MException("already logged in", userContext.getAccountId());
        //
        //        AccessApi aaa = M.l(AccessApi.class);
        //
        //        String userTicket = aaa.createUserTicket(user, pass);
        //        context.getSession().setString(SESSION_PARAMETER_NAME, userTicket);
        //
        //        Locale locale = context.getHttpRequest().getLocale();
        //        userContext = aaa.process(userTicket, locale);
        //        context.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, userContext);
    }

    public static void logout(CallContext context, String user, String pass) {

        //        AaaContext userContext = (AaaContext)
        // context.getAttribute(CONTEXT_PARAMETER_AAA_CONTEXT);
        //        if (userContext == null) return;
        //
        //        AccessApi aaa = M.l(AccessApi.class);
        //        aaa.release(userContext);

        context.getSession().remove(SESSION_PARAMETER_NAME);
        context.setAttribute(CONTEXT_PARAMETER_AAA_CONTEXT, null);
    }

    @Override
    public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) {}
}
