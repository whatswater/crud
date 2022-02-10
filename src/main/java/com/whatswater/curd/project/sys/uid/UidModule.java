package com.whatswater.curd.project.sys.uid;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.CrudConst;

public class UidModule implements Module {
    @Override
    public void register(ModuleInfo moduleInfo) {
        UidGeneratorService uidGeneratorService = new UidGeneratorService(CrudConst.WORKER_ID);
        moduleInfo.exportObject("uidGeneratorService", uidGeneratorService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

    }
}
