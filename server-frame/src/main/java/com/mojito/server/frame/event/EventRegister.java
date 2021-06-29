package com.mojito.server.frame.event;

import com.mojito.server.frame.annotation.EventListen;
import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.annotation.TaskDesc;
import com.mojito.server.frame.config.CollectEventHandler;
import com.mojito.server.frame.event.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author huangwei10
 * @create 2021/5/17
 */

public class EventRegister {

	private static final Logger log = LoggerFactory.getLogger(EventRegister.class);

	private boolean registerStatus = false;

	public EventRegister(){

	}


	/**
	 * 保证成功触发只有一次
	 * @param basePackage
	 */
	public synchronized void doRegist(String basePackage){
		if(!registerStatus){
			CollectEventHandler collectEventHandler = new CollectEventHandler(basePackage);
			List<EventHandler> eventHandlerBeans = collectEventHandler.getEventHandlerBean();
			for(EventHandler handler : eventHandlerBeans){
				try{
					registerHandler(handler);
				}catch (Exception e){
					log.error(e.getMessage(), e);
				}
			}
			setRegisterStatus(true);
		}
	}


	/**
	 * 此方法有问题 需要重新评估实现
	 * @param handler
	 */
	private void registerHandler(EventHandler handler) throws NoSuchMethodException {
		//遍历所以的继承类
		EventListen eventListenAnno = handler.getClass().getAnnotation(EventListen.class);
		if(null != eventListenAnno){
			String[] listenFilters = eventListenAnno.value();
			if(null == listenFilters || listenFilters.length == 0){
				//不允许用了标签但是为空的情况
				return;
			} else{
				for(String listenFilter : listenFilters){
					//每一个value
					String filter = listenFilter.trim();
					String[] eventFilters  = filter.split(",");
					String eventType = eventFilters[0].trim();
					Lock lock = handler.getClass().getDeclaredMethod("execute", Event.class).getAnnotation(Lock.class);
					EventCenter.getInstance().doRegister(eventType, handler,lock, null);
				}
			}

		} else {
			//是手动触发，没有监听事件,直接获取task注册，如TestTask1的TaskName为testTask1
			TaskDesc taskDesc = handler.getClass().getAnnotation(TaskDesc.class);
			if(null != taskDesc){
				EventCenter.getInstance().doRegister(taskDesc.value(), handler, null);
			} else {
				return;
			}
		}

	}

	public void setRegisterStatus(boolean registerStatus) {
		this.registerStatus = registerStatus;
	}
}
