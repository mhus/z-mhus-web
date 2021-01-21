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
package de.mhus.app.web.util.webspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import de.mhus.app.web.api.CallContext;
import de.mhus.app.web.api.CherryApi;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;

public class DirectoryWebSpace extends AbstractWebSpace {

    private IConfig cDir;
    private String[] indexes = new String[] {"index.html"};

    @Override
    public void start(CherryApi api) throws MException {
        super.start(api);
        cDir = getConfig().getObject("directory");
        if (cDir != null) {
            charsetEncoding = cDir.getString("characterEncoding", charsetEncoding);
            if (cDir.isProperty("indexes")) indexes = cDir.getString("indexes").split(",");
        }
    }

    @Override
    protected void doDeleteRequest(CallContext context) {}

    @Override
    protected void doPutRequest(CallContext context) {}

    @Override
    protected void doPostRequest(CallContext context) {}

    @Override
    protected void doHeadRequest(CallContext context) {
        String path = context.getHttpPath();
        path = MFile.normalizePath(path);
        File file = new File(getDocumentRoot(), path);
        if (!file.exists()) {
            sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
            return;
        }
        if (file.isDirectory()) {
            file = findIndex(file);
            if (file == null) {
                // TODO support directory indexing ?
                sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
                return;
            }
        }
        prepareHead(context, file);
    }

    @Override
    protected void doGetRequest(CallContext context) {
        String path = context.getHttpPath();
        path = MFile.normalizePath(path);
        File file = new File(getDocumentRoot(), path);
        if (!file.exists()) {
            sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
            return;
        }
        if (file.isDirectory()) {
            file = findIndex(file);
            if (file == null) {
                // TODO support directory indexing ?
                sendError(context, HttpServletResponse.SC_NOT_FOUND, null);
                return;
            }
        }
        prepareHead(context, file);

        try {
            FileInputStream is = new FileInputStream(file);
            OutputStream os = context.getOutputStream();
            MFile.copyFile(is, os);
            os.close();
            is.close();
        } catch (Throwable t) {
            log().w(file, t);
            sendError(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t);
        }
    }

    protected File findIndex(File dir) {

        for (String index : indexes) {
            File file = new File(dir, index);
            if (file.exists() && file.isFile()) return file;
        }

        return null;
    }

    protected void prepareHead(CallContext context, File file) {
        HttpServletResponse resp = context.getHttpResponse();
        resp.setContentLengthLong(file.length());
        resp.setCharacterEncoding(charsetEncoding);
        resp.setHeader("Last-Modified", MDate.toHttpHeaderDate(file.lastModified()));
        super.prepareHead(context, MFile.getFileExtension(file), file.getAbsolutePath());
    }
}
