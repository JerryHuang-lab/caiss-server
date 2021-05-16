package com.mojito.server.frame.event.exception;

/**
 * 事件执行异常
 * 
 * @author huangwei10
 * @create 2021/5/16
 *
 */
public class EventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4216846461379940939L;
	
	public EventException(String msg){
		super(msg);
	}

}
