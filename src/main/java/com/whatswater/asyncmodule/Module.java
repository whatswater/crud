package com.whatswater.asyncmodule;

public interface Module {
    /**
     * 向模块工厂注册模块，可以在此方法内设置模块依赖
     * @param moduleInfo 本模块的模块信息
     */
    void register(ModuleInfo moduleInfo);

    /**
     * 当有其他模块依赖本模块时（实现按需加载）
     * @param provider 生产者
     * @param consumer 消费者
     * @param name name
     */
    default void onBeRequired(ModuleInfo provider, ModuleInfo consumer, String... name) {

    };

    /**
     * 模块依赖满足事件，A依赖B模块的一个名为C的对象，当B模块导出C对象时，A模块的此方法将被调用
     * @param consumer 本模块的模块信息
     * @param provider 模块对象提供者的模块信息
     * @param name 模块对象名
     */
    void onResolved(
            ModuleInfo consumer,
            ModuleInfo provider,
            String name,
            Object obj
    );

    /**
     * 模块依赖未满足事件，A依赖B模块的一个名为C的对象，当B模块已经加载完毕时，还没有导出C对象，那么会调用A模块的此方法
     * @param consumer 本模块的模块信息
     * @param provider 模块对象提供者的模块信息
     * @param name 模块对象名
     */
    default void onMissed(
            ModuleInfo consumer,
            ModuleInfo provider,
            String name
    ) {}

    /**
     * 卸载模块，释放本模块的资源
     * @param provider 本模块的模块信息
     */
    default void onUninstall(ModuleInfo provider) {}
}
