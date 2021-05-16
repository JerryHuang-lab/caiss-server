package com.mojito.server.frame.event.result;

/**
 * @author hw
 * @create 2021/5/15
 */

public class EventHandlerResult extends Result{



    /**
     * 忽略失败任务
     */
    protected boolean ingore;

    protected boolean needRetry = true;



    /**
     * 结果是否需要重试
     * 一般只有失败时才需要读该值进行判断
     *
     * @return
     */
    public boolean isNeedRetry() {
        return needRetry;
    }

    /**
     * 是否忽略的结果
     * 一般指没的真正需要执行的内容或者结果
     *
     * @return
     */
    public boolean isIngore() {
        return ingore;
    }

    /**
     * 指示执行结果是否停止后续的事件处理
     *
     * @return
     */
    public boolean toStopEventHandling() {
        return false;
    }

    private static EventHandlerResult successResult = new EventHandlerResult(
            true, false, null, null, null);
    private static EventHandlerResult ingoreResult = new EventHandlerResult(true,
            true, null, null, null);

    /**
     * 得到成功结果
     * 成功是单例对象，可以任何获取而不会有任何内存压力
     *
     * @return
     */
    public static EventHandlerResult getSuccessResult() {
        return successResult;
    }

    /**
     * 得到失败结果
     * 有重试
     *
     * @param errorCode
     * @param errorMessage
     * @return
     */
    public static EventHandlerResult getFailureResult(String errorCode,
                                                     String errorMessage) {
        return new EventHandlerResult(false, false, errorCode, errorMessage,
                null);
    }

    protected EventHandlerResult(boolean succuss, boolean ingore,
                                String errorCode, String errorMessage, Exception exception) {
        super(succuss, errorCode, errorMessage, exception);
        //如果忽略则不重试
        if(ingore){
            this.needRetry = false;
        }
        this.ingore = ingore;

    }

    /**
     * 得到失败结果且不重试
     *
     * @param errorCode
     * @param errorMessage
     * @return
     */
    public static EventHandlerResult getFailureAndNoRetryResult(String errorCode,
                                                               String errorMessage) {
        EventHandlerResult result = new EventHandlerResult(false, false, errorCode, errorMessage,
                null);
        result.needRetry = false;
        return result;
    }

    /**
     * 得到忽略结果
     * 忽略是一种成功的特殊情况，一般情况下，其行为和成功一致
     * 特别情况下，会有特别的行为
     *
     * @return
     */
    public static EventHandlerResult getIgnoreResult() {
        return ingoreResult;
    }

    /**
     * 得到异常结果
     * 异常结果是一种特殊的失败情况，一般情况下，其行为和失败一致
     * 同时可以得到异常的实例
     * 默认需要重试
     *
     * @param exception
     * @return
     */
    public static EventHandlerResult getExceptionResult(Exception exception) {
        return new EventHandlerResult(false, false, null, null, exception);
    }

    /**
     * 得到异常结果
     * 异常结果是一种特殊的失败情况，一般情况下，其行为和失败一致
     * 同时可以得到异常的实例
     * 默认需要重试
     *
     * @param errorCode
     * @param errorMessage
     * @param exception
     * @return
     */
    public static EventHandlerResult getExceptionResult(String errorCode,
                                                       String errorMessage, Exception exception) {
        return new EventHandlerResult(false, false, errorCode, errorMessage,
                exception);
    }


}
