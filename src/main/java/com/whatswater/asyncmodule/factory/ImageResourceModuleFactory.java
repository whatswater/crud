package com.whatswater.asyncmodule.factory;


import com.whatswater.asyncmodule.*;
import com.whatswater.asyncmodule.util.ModuleUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageResourceModuleFactory implements ModuleFactory {
    public static final String IMAGE = "image";
    private final String foldRoot;
    public ImageResourceModuleFactory(String foldRoot) {
        this.foldRoot = foldRoot;
    }

    @Override
    public String getFactoryName() {
        return IMAGE;
    }

    @Override
    public Module createModule(String modulePath) throws Exception {
        return new ImageModule(foldRoot, modulePath);
    }

    public static class ImageModule implements Module {
        private final String foldRoot;
        private final String modulePath;
        private ImageModule(String foldRoot, String modulePath) {
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
            try {
                BufferedImage img = ImageIO.read(new File(path));
                moduleInfo.exportObject(img);
            } catch (IOException e) {
                throw new RuntimeException("加载图片文件失败");
            }
        }

        @Override
        public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

        }
    }
}
