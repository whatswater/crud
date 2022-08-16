package com.whatswater.sql.executor;


public interface ContextService<T extends ContextService<T>> {
    default <E extends ContextService<E>> E getContextService(E service) {
        return null;
    }
    default T getOriginService() {
        return null;
    }
    default Context getCurrentContext() {
        return null;
    }
}
