package com.whatswater.asyncmodule;

public interface ModuleEvent {
    void execute();
    ModuleInfo getConsumer();

    class RequireResolvedEvent implements ModuleEvent {
        private final ModuleInfo consumer;
        private final ModuleInfo provider;
        private final String requireName;
        private final Object object;

        public RequireResolvedEvent(ModuleInfo consumer, ModuleInfo provider, String requireName, Object object) {
            this.consumer = consumer;
            this.provider = provider;
            this.requireName = requireName;
            this.object = object;
        }

        @Override
        public void execute() {
            consumer.getModuleInstance().onResolved(consumer, provider, requireName, object);
        }

        @Override
        public ModuleInfo getConsumer() {
            return consumer;
        }

        public ModuleInfo getProvider() {
            return provider;
        }

        public String getRequireName() {
            return requireName;
        }

        public Object getObject() {
            return object;
        }
    }

    class RequireMissedEvent implements ModuleEvent {
        private final ModuleInfo consumer;
        private final ModuleInfo provider;
        private final String requireName;

        public RequireMissedEvent(ModuleInfo consumer, ModuleInfo provider, String requireName) {
            this.consumer = consumer;
            this.provider = provider;
            this.requireName = requireName;
        }

        @Override
        public void execute() {
            consumer.getModuleInstance().onMissed(consumer, provider, requireName);
        }

        @Override
        public ModuleInfo getConsumer() {
            return consumer;
        }

        public ModuleInfo getProvider() {
            return provider;
        }

        public String getRequireName() {
            return requireName;
        }
    }

    class ModuleUninstallEvent implements ModuleEvent {
        private final ModuleInfo consumer;
        private final ModuleInfo provider;

        public ModuleUninstallEvent(ModuleInfo consumer, ModuleInfo provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @Override
        public void execute() {
            consumer.getModuleInstance().onUninstall(provider);
        }

        @Override
        public ModuleInfo getConsumer() {
            return consumer;
        }

        public ModuleInfo getProvider() {
            return provider;
        }
    }
}
