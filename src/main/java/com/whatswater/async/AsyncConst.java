package com.whatswater.async;


public class AsyncConst {
    public static final String SETTER_PREFIX = "set_";
    public static final String ACCESS_PREFIX = "_$213$";

    public static final String OBJECT_CLASS_NAME = "java/lang/Object";
    public static final String OBJECT_CLASS_DESC = "Ljava/lang/Object;";
    public static final String THROWABLE_CLASS_NAME = "java/lang/Throwable";
    public static final String INIT_METHOD_NAME = "<init>";
    public static final String EMPTY_CONSTRUCTOR_DESC = "()V";

    public static final String RUNNABLE_INTERFACE_NAME = "java/lang/Runnable";
    public static final String TASK_INTERFACE_NAME = "com/whatswater/async/Task";
    public static final String TASK_CLASS_NAME = "Task";
    public static final String JAVA_FILE_SUFFIX = ".java";

    public static final String METHOD_NAME_MOVE_TO_NEXT = "moveToNext";
    public static final String METHOD_DESC_MOVE_TO_NEXT = "(I)V";

    public static final String HANDLER_CLASS_NAME = "com/whatswater/async/handler/AwaitTaskHandler";
    public static final String HANDLER_FIELD_DESC = "Lcom/whatswater/async/handler/AwaitTaskHandler;";
    public static final String HANDLER_CONSTRUCTOR_DESC = "(Lcom/whatswater/async/Task;I)V";

    public static final String FUTURE_INTERFACE_NAME = "io/vertx/core/Future";
    public static final String FUTURE_CLASS_NAME = "com/whatswater/async/future/TaskFutureImpl";
    public static final String FUTURE_FIELD_DESC = "Lcom/whatswater/async/future/TaskFutureImpl;";

    public static final String HANDLER_SUCCEEDED_METHOD_NAME = "succeeded";
    public static final String HANDLER_SUCCEEDED_METHOD_DESC = "()Z";
    public static final String HANDLER_GET_RESULT_METHOD_NAME = "getResult";
    public static final String HANDLER_GET_RESULT_METHOD_DESC = "()Ljava/lang/Object;";
    public static final String HANDLER_GET_THROWABLE_METHOD_NAME = "getThrowable";
    public static final String HANDLER_GET_THROWABLE_METHOD_DESC = "()Ljava/lang/Throwable;";
    public static final String FUTURE_TRY_FAIL_METHOD_NAME = "tryFail";
    public static final String FUTURE_TRY_FAIL_METHOD_DESC = "(Ljava/lang/Throwable;)Z";
    public static final String FUTURE_ON_COMPLETE_METHOD_NAME = "onComplete";
    public static final String FUTURE_ON_COMPLETE_METHOD_DESC = "(Lio/vertx/core/Handler;)Lio/vertx/core/Future;";
    public static final String FUTURE_CLASS_TRY_COMPLETE_METHOD_NAME = "tryComplete";
    public static final String FUTURE_CLASS_TRY_COMPLETE_METHOD_DESC = "(Ljava/lang/Object;)Z";
}
