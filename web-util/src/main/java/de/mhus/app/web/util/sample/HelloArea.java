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

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.mhus.app.web.api.CallContext;
import de.mhus.app.web.api.VirtualHost;
import de.mhus.app.web.api.WebArea;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.errors.MException;

public class HelloArea implements WebArea {

    @Override
    public boolean doRequest(UUID instance, CallContext call) throws MException {

        try {
            call.getWriter().write("Hello " + new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void doInitialize(UUID instance, VirtualHost vHost, INode config) {}
}
