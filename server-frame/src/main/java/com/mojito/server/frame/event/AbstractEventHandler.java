package com.mojito.server.frame.event;

import com.mojito.server.frame.event.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基本的事件处理器
 * 
 * @author huangwei10
 *
 */
public abstract class AbstractEventHandler implements EventHandler {

	private final static Logger logger = LoggerFactory.getLogger("AbstractEventHandler");
	
	public void init(){
		
	}
	
	@Override
	public String toString(){
		return this.getClass().getName();
	}

}
