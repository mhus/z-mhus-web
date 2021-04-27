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
package de.mhus.app.web.util.sample;

import java.util.UUID;

import de.mhus.app.web.api.InternalCallContext;
import de.mhus.app.web.api.VirtualHost;
import de.mhus.app.web.api.WebFilter;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.errors.MException;

public class TraceFilter extends MLog implements WebFilter {

    private static final String CALL_START = "filter_TraceFilter_start";

    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
        long start = System.currentTimeMillis();
        call.setAttribute(CALL_START, start);
        log().d("access", call.getHttpHost(), call.getHttpMethod(), call.getHttpPath());
        return true;
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
    public void doInitialize(UUID instance, VirtualHost vHost, INode config) {}
}
