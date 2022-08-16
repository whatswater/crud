package com.whatswater.curd;


import com.whatswater.asyncmodule.AbstractModuleAdaptor;

import java.awt.image.BufferedImage;

public class ModuleTest extends AbstractModuleAdaptor {
    public static final String FILE = "image:81d1aac379310a5566c1439eb44543a9802610de.jpg";

    @Override
    public void init() {
        require(FILE);
    }

    @Override
    public void resolve() {
        BufferedImage bufferedImage = getResolved(FILE);
    }
}
