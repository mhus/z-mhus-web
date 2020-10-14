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
package de.mhus.app.web.api;

import java.util.UUID;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public interface WebFilter {

    void doInitialize(UUID instance, VirtualHost vHost, IConfig config) throws MException;

    /**
     * Return true if the page can be displayed. false will prevent processing of the page
     *
     * @param instance to identify the instance in stateless environments like services
     * @param call
     * @return true if ok
     * @throws MException
     */
    boolean doFilterBegin(UUID instance, InternalCallContext call) throws MException;

    void doFilterEnd(UUID instance, InternalCallContext call) throws MException;
}
