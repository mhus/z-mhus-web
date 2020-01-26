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
package de.mhus.cherry.web.api;

import java.io.OutputStream;

public interface InternalCallContext extends CallContext {

    /**
     * Insert a outputs tream in the chain. This stream will become the next current os.
     *
     * @param os
     */
    void setOutputStream(OutputStream os);

    void setRemoteIp(String remoteIp);
}
