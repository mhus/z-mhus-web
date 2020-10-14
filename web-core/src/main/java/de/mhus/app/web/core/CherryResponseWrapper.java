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
package de.mhus.app.web.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CherryResponseWrapper implements HttpServletResponse {

    private HttpServletResponse instance;
    private CherryServletOutputStream stream;
    private CherryServletPrintWriter writer;

    public CherryResponseWrapper(HttpServletResponse res) {
        instance = res;
    }

    @Override
    public String getCharacterEncoding() {
        return instance.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return instance.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (stream == null) {
            stream = new CherryServletOutputStream(instance.getOutputStream());
        }
        return stream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new CherryServletPrintWriter(instance.getOutputStream());
        }
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        instance.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        instance.setContentLength(len);
    }

    @Override
    public void setContentType(String type) {
        instance.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        instance.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return instance.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) writer.flush();
        if (stream != null) stream.flush();
        instance.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        instance.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return instance.isCommitted();
    }

    @Override
    public void reset() {
        instance.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        instance.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return instance.getLocale();
    }

    @Override
    public void addCookie(Cookie cookie) {
        instance.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return instance.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return instance.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return instance.encodeRedirectURL(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeUrl(String url) {
        return instance.encodeUrl(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeRedirectUrl(String url) {
        return instance.encodeRedirectUrl(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        instance.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        instance.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        instance.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        instance.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        instance.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        instance.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        instance.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        instance.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        instance.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
        instance.setStatus(sc);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setStatus(int sc, String sm) {
        instance.setStatus(sc, sm);
    }

    @Override
    public void setContentLengthLong(long len) {
        instance.setContentLengthLong(len);
    }

    @Override
    public int getStatus() {
        return instance.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return instance.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return instance.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return instance.getHeaderNames();
    }
}
