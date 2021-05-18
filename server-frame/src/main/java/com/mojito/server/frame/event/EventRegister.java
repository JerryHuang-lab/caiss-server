package com.mojito.server.frame.event;

import com.mojito.server.frame.annotation.EventListen;
import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.annotation.TaskDesc;
import com.mojito.server.frame.event.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author huangwei10
 * @create 2021/5/17
 */

public class EventRegister {

	private static final Logger log = LoggerFactory.getLogger(EventRegister.class);

	@Autowired
	private EventHandler[] handlers;

	public void init(){
		for(EventHandler handler : handlers){
			try{
				registerHandler(handler);
			}catch (Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 此方法有问题 需要重新评估实现
	 * @param handler
	 */
	private void registerHandler(EventHandler handler) throws NoSuchMethodException {
		//遍历所以的继承类
		EventListen eventListenAnno = AnnotationUtils.getAnnotation(handler.getClass(), EventListen.class);
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
					Lock lock = AnnotationUtils.getAnnotation(handler.getClass().getMethod("execute", Event.class),
							Lock.class);
					EventCenter.getInstance().doRegister(eventType, handler,lock, null);
				}
			}

		} else {
			//是手动触发，没有监听事件,直接获取task注册，如TestTask1的TaskName为testTask1
			TaskDesc taskDesc = AnnotationUtils.getAnnotation(handler.getClass(), TaskDesc.class);
			if(null != taskDesc){
				EventCenter.getInstance().doRegister(taskDesc.value(), handler, null);
			} else {
				return;
			}
		}

	}
}
