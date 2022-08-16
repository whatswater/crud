package com.whatswater.asyncmodule.factory;


import cn.hutool.core.io.FileUtil;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleFactory;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.asyncmodule.util.ModuleUtil;
import com.zandero.utils.extra.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonModuleFactory implements ModuleFactory {
    public static final String JSON = "json";
    private final String foldRoot;
    public JsonModuleFactory(String foldRoot) {
        this.foldRoot = foldRoot;
    }

    @Override
    public String getFactoryName() {
        return JSON;
    }

    @Override
    public Module createModule(String modulePath) throws Exception {
        return new JsonModule(foldRoot, modulePath);
    }

    public static class JsonModule implements Module {
        private final String foldRoot;
        private final String modulePath;

        public JsonModule(String foldRoot, String modulePath) {
            this.foldRoot = foldRoot;
            this.modulePath = modulePath;
        }

        @Override
        public void register(ModuleInfo moduleInfo) {
            String url = ModuleUtil.getModulePathParam(modulePath);
            String root = foldRoot;
            if (!root.endsWith("/")) {
                root = root + "/";
            }
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            String path = root + url;
            String content = FileUtil.readString(new File(path), StandardCharsets.UTF_8);
            moduleInfo.exportObject(JsonUtils.fromJson(content, Map.class));
        }

        @Override
        public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

        }
    }
}
