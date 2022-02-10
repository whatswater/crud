package com.whatswater.curd.project.sys.admin;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class AdminModule implements Module {
    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.exportObject("adminService", new AdminService());
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

    }
}
