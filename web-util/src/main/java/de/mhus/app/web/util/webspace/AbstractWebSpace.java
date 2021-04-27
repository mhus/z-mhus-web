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
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import de.mhus.app.web.api.CallContext;
import de.mhus.app.web.api.CherryApi;
import de.mhus.app.web.api.TypeDefinition;
import de.mhus.app.web.api.TypeHeader;
import de.mhus.app.web.api.VirtualWebSpace;
import de.mhus.app.web.api.WebArea;
import de.mhus.app.web.api.WebFilter;
import de.mhus.app.web.util.AbstractVirtualHost;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.io.FileWatch;
import de.mhus.lib.core.node.INode;
import de.mhus.lib.core.node.INodeFactory;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.api.util.OsgiBundleClassLoader;

public abstract class AbstractWebSpace extends AbstractVirtualHost implements VirtualWebSpace {

    protected File root;
    private INode cServer;
    private File configRoot;
    private File documentRoot;
    private FileWatch configWatch;
    private Date updated;
    protected HashMap<String, TypeDefinition> types = new HashMap<>();

    public void setRoot(String rootPath) throws MException {
        root = new File(rootPath);
    }

    @Override
    public void start(CherryApi api) throws MException {
        setUpdated();
        if (!root.exists()) throw new MException("root for webspace not exists", root);
        if (!root.isDirectory()) throw new MException("root for webspace not a directory", root);

        configRoot = new File(root, "conf"); // default configuration directory
        if (!configRoot.exists() || !configRoot.isDirectory())
            configRoot = root; // fall back to root directory

        String configFile = prepareConfigName("server");
        log().i("start web space", configFile, getClass().getCanonicalName());
        config = M.l(INodeFactory.class).find(configRoot, configFile);
        if (config == null) throw new MException("config for webspace not found", root);
        // get server config
        cServer = config.getObject("server");
        if (cServer == null) throw new MException("server in config not found", root);
        // get alias
        setConfigAliases(INode.toStringArray(cServer.getObjectList("aliases"), "value"));
        // set name
        name = getFirstAlias(); // default
        name = cServer.getString("name", name);
        // get config root
        if (cServer.isProperty("configurationRoot"))
            configRoot = findProjectFile(cServer.getString("configurationRoot"));
        // document root
        documentRoot = findProjectFile(cServer.getString("documentRoot", "html"));
        // trace options
        traceErrors = cServer.getBoolean("traceErrors", true);
        traceAccess = cServer.getBoolean("traceAccess", false);
        // defaultMimeType
        defaultMimeType = cServer.getString("defaultMimeType", defaultMimeType);
        // load filters
        OsgiBundleClassLoader loader = new OsgiBundleClassLoader();
        for (INode filterDef : cServer.getObjectList("filters")) {
            String filterClazzName = filterDef.getString("class");
            try {
                Class<?> clazz = loader.loadClass(filterClazzName);
                WebFilter filter = (WebFilter) clazz.getDeclaredConstructor().newInstance();
                filter.doInitialize(getInstanceId(), this, filterDef);
                addFilter(filter);
            } catch (ClassNotFoundException e) {
                throw new MException("filter not found", filterClazzName);
            } catch (Throwable e) {
                throw new MException("can't instanciate filter", filterClazzName, e);
            }
        }
        // load active areas
        for (INode areaDef : cServer.getObjectList("areas")) {
            String areaPath = areaDef.getString("path");
            String areaClazzName = areaDef.getString("class");
            try {
                Class<?> clazz = loader.loadClass(areaClazzName);
                WebArea area = (WebArea) clazz.getDeclaredConstructor().newInstance();
                area.doInitialize(getInstanceId(), this, areaDef);
                addArea(areaPath, area);
            } catch (ClassNotFoundException e) {
                throw new MException("area not found", areaClazzName);
            } catch (Throwable e) {
                throw new MException("can't instanciate area", areaClazzName, e);
            }
        }

        for (INode typeDef : cServer.getObjectList("types")) {
            TypeDefinition type = new TypeDefinition();
            type.setName(typeDef.getString("name"));
            if (typeDef.isObject("extends"))
                type.setExtends(
                        INode.toStringArray(typeDef.getObject("extends").getObjects(), "value"));
            type.setMimeType(typeDef.getString("mimetype", null));

            if (typeDef.isObject("headers")) {
                INode headersDef = typeDef.getObject("headers");
                for (INode header : headersDef.getObjects()) {
                    TypeHeader obj = api.createTypeHeader(header);
                    if (obj != null) type.addHeader(obj);
                }
            }
            types.put(type.getName(), type);
            log().i(
                            "add Type",
                            type.getName(),
                            type.getExtends(),
                            type.getMimeType(),
                            type.getHeaders());
        }

        if (cServer.getBoolean("watchConfiguration", true)) {
            configWatch =
                    new FileWatch(
                                    new File(configRoot, configFile + ".json"),
                                    new FileWatch
                                            .Listener() { // TODO could also be another extension

                                        @Override
                                        public void onFileWatchError(
                                                FileWatch fileWatch, Throwable t) {}

                                        @Override
                                        public void onFileChanged(FileWatch fileWatch) {
                                            log().i("configuration changed: restart", getName());
                                            api.restart(AbstractWebSpace.this);
                                        }
                                    })
                            .doStart();
        }
    }

    protected void setUpdated() {
        updated = new Date();
    }

    public File findProjectFile(String path) {
        if (path.startsWith("/")) {
            if (MSystem.isWindows()) return new File(path.substring(1));
            else return new File(path);
        }
        return new File(root, path);
    }

    @Override
    public void stop(CherryApi api) {
        config = null;
        cServer = null;
        if (configWatch != null) configWatch.cancel();
        configWatch = null;
    }

    @Override
    public File getConfigRoot() {
        return configRoot;
    }

    @Override
    public File getProjectRoot() {
        return root;
    }

    @Override
    public File getDocumentRoot() {
        return documentRoot;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public File findFile(String path) {
        path = path.replace("${profile}", getProfile());
        if (path.startsWith("/")) return new File(path);
        return new File(configRoot, path);
    }

    @Override
    public TypeDefinition getType(CallContext context, String t) {
        t = t.trim().toLowerCase();
        return types.get(t);
    }

    @Override
    public TypeDefinition prepareHead(CallContext context, String t, boolean fallback) {
        t = t.trim().toLowerCase();
        TypeDefinition type = types.get(t);
        if (type == null && fallback) type = types.get("*");
        if (type == null) return null;
        HttpServletResponse resp = context.getHttpResponse();
        addHeaders(type, resp, 0);
        if (type.getMimeType() != null) {
            resp.setContentType(type.getMimeType());
        }
        return type;
    }

    protected void prepareHead(CallContext context, String fileSuffix, String path) {
        TypeDefinition type = prepareHead(context, "." + fileSuffix, true);
        if (type != null && type.getMimeType() == null) {
            HttpServletResponse resp = context.getHttpResponse();
            String mimeType = getMimeType(fileSuffix);
            if (mimeType != null) resp.setContentType(mimeType);
        }
    }

    private void addHeaders(TypeDefinition type, HttpServletResponse resp, int level) {
        if (level > 20) return;
        for (TypeHeader header : type.getHeaders()) header.appendTo(resp);
        String[] refs = type.getExtends();
        if (refs != null)
            for (String ref : refs) {
                TypeDefinition type2 = types.get(ref);
                if (type2 != null) addHeaders(type2, resp, level + 1);
            }
    }
}
