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
package de.mhus.app.web.api;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.osgi.framework.Bundle;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.errors.MException;

public interface VirtualHost {

    void sendError(CallContext call, int sc, Throwable t);

    void doRequest(InternalCallContext call);

    /**
     * Get WebSpace configuration'
     *
     * @return Config object
     */
    INode getConfig();

    /**
     * Web Space specific properties.
     *
     * @return Properties container
     */
    IProperties getProperties();

    Set<String> getVirtualHostAliases();

    void start(CherryApi api) throws MException;

    void stop(CherryApi api);

    void setBundle(Bundle bundle);

    Bundle getBundle();

    String getMimeType(String file);

    String getName();

    String getCharsetEncoding();

    boolean isTraceAccess();

    boolean isTraceErrors();

    Date getUpdated();

    File findFile(String path);

    TypeDefinition prepareHead(CallContext context, String type, boolean fallback);

    TypeDefinition getType(CallContext context, String type);
}
