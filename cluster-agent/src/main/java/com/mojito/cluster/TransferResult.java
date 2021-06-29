package com.mojito.cluster;

import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import com.mojito.server.frame.event.result.Result;

/**
 * 对外暴露的结果，区别于eventHandlerResult
 * @author huangwei10
 * @create 2021/6/28
 */

public class TransferResult  extends Result {


	protected TransferResult() {
		super(true, null, null, null);
	}

	public TransferResult(EventHandlerResult eventHandlerResult){
		super(eventHandlerResult.isSuccess(),eventHandlerResult.getErrorCode(),eventHandlerResult.getErrorMessage(),
				eventHandlerResult.getException());
	}

	public TransferResult(EventTriggerResult eventTriggerResult){
		super(eventTriggerResult.isSuccess(),eventTriggerResult.getErrorCode(),eventTriggerResult.getErrorMessage(),
				eventTriggerResult.getException());
	}
}
