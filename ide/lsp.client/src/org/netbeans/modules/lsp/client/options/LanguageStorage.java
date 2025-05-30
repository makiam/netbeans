/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.lsp.client.options;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.event.ChangeEvent;
import org.netbeans.modules.lsp.client.debugger.api.RegisterDAPBreakpoints;
import org.eclipse.tm4e.core.internal.grammar.raw.RawGrammarReader;
import org.eclipse.tm4e.core.registry.IGrammarSource;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.modules.textmate.lexer.TextmateTokenId;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 *
 * @author lahvac
 */
public class LanguageStorage {

    /**
     * Startup handler for language store. This handler reapplies the language
     * descriptions at startup so that the runtime view matches the one expected
     * by the running IDE. This is mostly relevant when the IDE requires new
     * files (like the MultiView description when that feature was introduced).
     */
    @OnStart
    public static class StartupHandler implements Runnable {

        @Override
        public void run() {
            // load language definitions and reapply them
            store(load());
        }

    }

    private static final String KEY = "language.descriptions";

    static List<LanguageDescription> load() {
        String descriptions = NbPreferences.forModule(LanguageServersPanel.class).get(KEY, "[]");
        return Arrays.stream(new Gson().fromJson(descriptions, LanguageDescription[].class)).collect(Collectors.toList());
    }

    @Messages("Source=&Source")
    static void store(List<LanguageDescription> languages) {
        Set<String> originalMimeTypes = load().stream().map(ld -> ld.mimeType).collect(Collectors.toSet());
        Set<String> mimeTypesToClear = new HashSet<>(originalMimeTypes);

        FileUtil.runAtomicAction((Runnable) () -> {
            FileObject mimeResolver = FileUtil.getConfigFile("Services/MIMEResolver");

            if (mimeResolver == null) {
                try {
                    mimeResolver = FileUtil.createFolder(FileUtil.getConfigRoot(), "Services/MIMEResolver");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            for (FileObject children : mimeResolver.getChildren()) {
                if ("synthetic".equals(children.getAttribute(LanguageServersPanel.class.getName()))) {
                    try {
                        children.delete();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            for (LanguageDescription description : languages) {
                try {
                    FileObject resolver = mimeResolver.getFileObject(description.id + ".xml");
                    if (resolver != null) {
                        //TODO: should happen?
                        resolver.delete();
                    }
                    resolver = mimeResolver.createData(description.id + ".xml");
                    Class<?> mimeResolverClass = Class.forName("org.openide.filesystems.MIMEResolver");
                    Method mimeResolverCreate = mimeResolverClass.getDeclaredMethod("create", FileObject.class);
                    resolver.setAttribute("methodvalue:instanceCreate", mimeResolverCreate);
                    resolver.setAttribute("instanceClass", "org.openide.filesystems.MIMEResolver");
                    resolver.setAttribute("mimeType", description.mimeType);
                    int c = 0;
                    for (String ext : description.extensions.split(" +")) {
                        resolver.setAttribute("ext." + c++, ext);
                    }
                    resolver.setAttribute(LanguageServersPanel.class.getName(), "synthetic");
                    FileObject syntax = FileUtil.getConfigFile("Editors/" + description.mimeType + "/syntax.json");
                    if (syntax != null) {
                        syntax.delete();
                    }
                    syntax = FileUtil.getConfigFile("Editors/" + description.mimeType + "/syntax.xml");
                    if (syntax != null) {
                        syntax.delete();
                    }
                    String ext = description.syntaxGrammar.substring(Math.max(0, description.syntaxGrammar.length() - ".json".length())).equalsIgnoreCase(".json") ? "json" : "xml";
                    syntax = FileUtil.createData(FileUtil.getConfigRoot(), "Editors/" + description.mimeType + "/syntax." + ext);
                    File grammar = new File(description.syntaxGrammar);
                    syntax.setAttribute("textmate-grammar", findScope(grammar));
                    try (InputStream in = new FileInputStream(grammar);
                         OutputStream out = syntax.getOutputStream()) {
                        FileUtil.copy(in, out);
                    }
                    FileObject loader = FileUtil.getConfigFile("Loaders/" + description.mimeType + "/Factories/data-object.instance");
                    if (loader != null) {
                        loader.delete();
                    }
                    loader = FileUtil.createData(FileUtil.getConfigRoot(), "Loaders/" + description.mimeType + "/Factories/data-object.instance");
                    loader.setAttribute("position", 300);
                    Class<?> dataLoaderPoolClass = GenericDataObject.class;
                    Method dataLoaderPoolFactory = dataLoaderPoolClass.getDeclaredMethod("factory");
                    //TODO: display name
                    loader.setAttribute("methodvalue:instanceCreate", dataLoaderPoolFactory);
                    loader.setAttribute("instanceOf", DataObject.Factory.class.getName());
                    loader.setAttribute("dataObjectClass", GenericDataObject.class.getName());
                    loader.setAttribute("mimeType", description.mimeType);

                    deleteConfigFileIfExists("Editors/" + description.mimeType + "/MultiView/source.instance");
                    FileObject multiViewRegistration = FileUtil.createData(FileUtil.getConfigRoot(), "Editors/" + description.mimeType + "/MultiView/source.instance");
                    Method createMultiViewDescription = MultiViewFactory.class.getDeclaredMethod("createMultiViewDescription", Map.class);
                    multiViewRegistration.setAttribute("methodvalue:instanceCreate", createMultiViewDescription);
                    multiViewRegistration.setAttribute("instanceClass", "org.netbeans.core.multiview.ContextAwareDescription");
                    multiViewRegistration.setAttribute("class", GenericDataObject.class.getName());
                    multiViewRegistration.setAttribute("mimeType", description.mimeType);
                    multiViewRegistration.setAttribute("displayName", Bundle.Source());
                    multiViewRegistration.setAttribute("preferredID", "lsp.source");
                    multiViewRegistration.setAttribute("persistenceType", 1);
                    multiViewRegistration.setAttribute("position", 100);
                    multiViewRegistration.setAttribute("method", "createEditor");

                    FileObject icon = FileUtil.getConfigFile("Loaders/" + description.mimeType + "/Factories/icon.png");
                    if (icon != null) {
                        icon.delete();
                    }
                    File iconFile = description.icon != null ? new File(description.icon) : null;
                    if (iconFile != null && iconFile.isFile()) {
                        icon = FileUtil.createData(FileUtil.getConfigRoot(), "Loaders/" + description.mimeType + "/Factories/icon.png");
                        try (InputStream in = new FileInputStream(iconFile);
                             OutputStream out = icon.getOutputStream()) {
                            FileUtil.copy(in, out);
                        }

                        loader.setAttribute("iconBase", icon.getNameExt());
                        multiViewRegistration.setAttribute("iconBase", icon.getNameExt());
                    }

                    if (description.languageServer != null && !description.languageServer.isEmpty()) {
                        FileObject langServer = FileUtil.createData(FileUtil.getConfigRoot(), "Editors/" + description.mimeType + "/org-netbeans-modules-lsp-client-options-GenericLanguageServer.instance");
                        langServer.setAttribute("command", description.languageServer.split(" "));
                        if (description.name != null) {
                            langServer.setAttribute("name", description.name);
                        }
                    }

                    deleteConfigFileIfExists("Editors/" + description.mimeType + "/generic-breakpoints.instance");
                    deleteConfigFileIfExists("Editors/" + description.mimeType + "/GlyphGutterActions/generic-toggle-breakpoint.shadow");

                    if (description.debugger) {
                        FileObject genericBreakpoints = FileUtil.createData(FileUtil.getConfigRoot(), "Editors/" + description.mimeType + "/generic-breakpoints.instance");

                        genericBreakpoints.setAttribute("instanceOf", RegisterDAPBreakpoints.class.getName());
                        Method newInstance = RegisterDAPBreakpoints.class.getDeclaredMethod("newInstance");
                        genericBreakpoints.setAttribute("methodvalue:instanceCreate", newInstance);

                        FileObject genericGutterAction = FileUtil.createData(FileUtil.getConfigRoot(), "Editors/" + description.mimeType + "/GlyphGutterActions/generic-toggle-breakpoint.shadow");

                        genericGutterAction.setAttribute("originalFile", "Actions/Debug/org-netbeans-modules-debugger-ui-actions-ToggleBreakpointAction.instance");
                        genericGutterAction.setAttribute("position", 500);
                    } else {
                        //TODO: remove
                    }

                    mimeTypesToClear.remove(description.mimeType);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            for (String mimeType : mimeTypesToClear) {
                try {
                    deleteConfigFileIfExists("Editors/" + mimeType + "/syntax.json");
                    deleteConfigFileIfExists("Editors/" + mimeType + "/org-netbeans-modules-lsp-client-options-GenericLanguageServer.instance");
                    deleteConfigFileIfExists("Loaders/" + mimeType + "/Factories/data-object.instance");
                    deleteConfigFileIfExists("Editors/" + mimeType + "/generic-breakpoints.instance");
                    deleteConfigFileIfExists("Editors/" + mimeType + "/GlyphGutterActions/generic-toggle-breakpoint.shadow");
                    deleteConfigFileIfExists("Editors/" + mimeType + "/MultiView/source.instance");
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });

        try {
            Method resetCache = Class.forName("org.openide.filesystems.MIMESupport").getDeclaredMethod("resetCache");
            resetCache.setAccessible(true);
            resetCache.invoke(null);

            GenericDataObject.invalidate();

            Method fireChangeEvent = DataLoaderPool.class.getDeclaredMethod("fireChangeEvent", ChangeEvent.class);
            fireChangeEvent.setAccessible(true);
            fireChangeEvent.invoke(DataLoaderPool.getDefault(), new ChangeEvent(DataLoaderPool.getDefault()));

            TextmateTokenId.LanguageHierarchyImpl.refreshGrammars();

            Class<?> providerRegistry = Class.forName("org.netbeans.modules.navigator.ProviderRegistry", false, NavigatorPanel.class.getClassLoader());
            Method getInstance = providerRegistry.getDeclaredMethod("getInstance");
            getInstance.setAccessible(true);
            Object providerRegistryInstance = getInstance.invoke(null);

            if (providerRegistryInstance != null) {
                Field file2Providers = providerRegistry.getDeclaredField("file2Providers");
                file2Providers.setAccessible(true);
                Map<?,?> file2ProvidersInstance = (Map<?,?>) file2Providers.get(providerRegistryInstance);
                if (file2ProvidersInstance != null) {
                    file2ProvidersInstance.clear();
                }
            }
        } catch (ReflectiveOperationException ex) {
            Exceptions.printStackTrace(ex);
        }

        NbPreferences.forModule(LanguageServersPanel.class).put(KEY, new Gson().toJson(languages));
    }

    private static void deleteConfigFileIfExists(String path) throws IOException {
        FileObject file = FileUtil.getConfigFile(path);

        if (file != null) {
            file.delete();
        }
    }

    private static String findScope(File grammar) throws Exception {
        return RawGrammarReader.readGrammar(IGrammarSource.fromFile(grammar.toPath())).getScopeName();
    }

    public static class LanguageDescription {

        public String id;
        public String extensions;
        public String syntaxGrammar;
        public String languageServer;
        public String name;
        public String icon;
        public String mimeType;
        public boolean debugger;

        public LanguageDescription() {
            this.id = null;
            this.extensions = null;
            this.syntaxGrammar = null;
            this.languageServer = null;
            this.name = null;
            this.icon = null;
            this.debugger = false;
            this.mimeType = null;
        }

        public LanguageDescription(String id, String extensions, String syntaxGrammar, String languageServer, String name, String icon, boolean debugger) {
            this.id = id;
            this.extensions = extensions;
            this.syntaxGrammar = syntaxGrammar;
            this.languageServer = languageServer;
            this.name = name;
            this.icon = icon;
            this.debugger = debugger;
            this.mimeType = "text/x-ext-" + id;
        }

    }
}
