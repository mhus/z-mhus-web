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

import java.util.UUID;

import de.mhus.app.web.api.InternalCallContext;
import de.mhus.app.web.api.VirtualHost;
import de.mhus.app.web.api.WebFilter;
import de.mhus.lib.core.M;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.osgi.api.util.OsgiBundleClassLoader;

public class FilterToMApi implements WebFilter {

    private IConfig config;
    private String serviceName;
    private Class<?> serviceClass;
    private WebFilter webFilter;
    private VirtualHost vHost;
    private UUID instanceId = UUID.randomUUID();

    @Override
    public void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException {
        this.config = config.getObject("config");
        serviceName = config.getString("service");
        this.vHost = vHost;
    }

    @Override
    public boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException {
        check();
        if (webFilter == null) throw new NotFoundException("service not found", serviceName);
        return webFilter.doFilterBegin(instanceId, call);
    }

    @Override
    public void doFilterEnd(UUID instance, InternalCallContext call) throws MException {
        check();
        if (webFilter == null) throw new NotFoundException("service not found", serviceName);
        webFilter.doFilterEnd(instanceId, call);
    }

    private synchronized void check() {
        if (webFilter == null) {
            OsgiBundleClassLoader loader = new OsgiBundleClassLoader();
            try {
                serviceClass = loader.loadClass(serviceName);
                webFilter = (WebFilter) M.l(serviceClass);
                webFilter.doInitialize(instanceId, vHost, config);
            } catch (Throwable e) {
                MLogUtil.log().e(serviceName, e);
            }
        }
    }
}
