package com.mojito.server.frame.event.result;

/**
 * @author hw
 * @create 2021/5/15
 */

public class Result {

    protected boolean succuss;
    public String getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public Exception getException() {
        return exception;
    }

    protected String errorCode;
    protected String errorMessage;
    protected Exception exception;
    protected Result innerResult;
    public Result getInnerResult() {
        return innerResult;
    }
    public void setInnerResult(Result innerResult) {
        this.innerResult = innerResult;
    }
    public boolean isSuccess() {
        return succuss;
    }
    protected Result(boolean succuss, String errorCode, String errorMessage, Exception exception){
        this.succuss = succuss;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exception = exception;
        if(this.errorMessage == null && exception != null){
            this.errorMessage = exception.getMessage();
        }
        if(this.errorMessage == null && exception == null){
            this.errorMessage = "exceptionMessage.null";
        }
        if(this.errorCode == null && exception != null){
            this.errorCode = "exception";
        }
        if(this.errorCode == null && exception == null){
            this.errorCode = "exception.null";
        }
    }

    private static Result successResult = new Result(true, null, null, null);

    public static Result getSuccessResult(){ return successResult; }
    public static Result getFailureResult(String errorCode, String errorMessage){
        return new Result(false, errorCode, errorMessage, null);
    }
    public static Result getExceptionResult(Exception exception){
        return new Result(false, null, null, exception);
    }
    public static Result getExceptionResult(String errorCode, String errorMessage, Exception exception){
        return new Result(false, errorCode, errorMessage, exception);
    }


}
