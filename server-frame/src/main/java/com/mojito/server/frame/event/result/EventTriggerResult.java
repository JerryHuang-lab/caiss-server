package com.mojito.server.frame.event.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hw
 * @create 2021/5/15
 */
@Data
public class EventTriggerResult extends Result{



    public EventTriggerResult(){
        super(true, null, null, null);
    }

    private boolean isHandled;

    /**
     * 触发结果后面可以有多个处理结果， 即事件监听被多个处理
     */
    private List<EventHandlerResult> handlerResults;


    @Override
    public String getErrorCode() {
        if(errorCode == null && handlerResults != null){
            StringBuilder sBuilder = null;
            for(EventHandlerResult result : handlerResults){
                if(!result.isSuccess()){
                    if(sBuilder == null){
                        sBuilder = new StringBuilder();
                        sBuilder.append(result.getErrorCode());
                    } else {
                        sBuilder.append("|");
                        sBuilder.append(result.getErrorCode());
                    }
                }
            }
            if(sBuilder == null){
                errorCode = "none";
            } else {
                errorCode = sBuilder.toString();
            }
        }
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        if(errorMessage == null && handlerResults != null){
            StringBuilder sBuilder = null;
            for( EventHandlerResult result: handlerResults){
                if(!result.isSuccess()){
                    if(sBuilder == null){
                        sBuilder = new StringBuilder();
                        sBuilder.append(result.getErrorMessage());
                    } else {
                        sBuilder.append("|");
                        sBuilder.append(result.getErrorMessage());
                    }
                }
            }
            if(sBuilder == null){
                errorMessage = "none";
            } else {
                errorMessage = sBuilder.toString();
            }
        }
        return errorMessage;
    }


    public void addEventHandlerResult(EventHandlerResult handlerResult) {
        if(null == handlerResults){
            handlerResults = new ArrayList<>();
        }
        handlerResults.add(handlerResult);
    }
}
