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
package de.mhus.cherry.web.api;

import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CallContext {

    String REQUEST_ATTRIBUTE_NAME = "__cherry_call_context";
    String REQUEST_REMOTE_IP = "__remote_ip";

    HttpServletRequest getHttpRequest();

    HttpServletResponse getHttpResponse();

    VirtualHost getVirtualHost();

    String getHttpPath();

    Servlet getHttpServlet();

    String getHttpMethod();

    WebSession getSession();

    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    String getSessionId();

    boolean isSession();

    String getHttpHost();

    String getRemoteIp();

    /**
     * Use this to get the output stream from http response to get the filter chain stream.
     *
     * @return current output stream
     */
    OutputStream getOutputStream();

    /**
     * This will return a writer bound to the output stream
     *
     * @return The writer
     */
    Writer getWriter();
}
