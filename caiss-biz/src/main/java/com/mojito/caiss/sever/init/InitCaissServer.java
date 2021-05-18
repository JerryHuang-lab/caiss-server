package com.mojito.caiss.sever.init;


import com.mojito.server.frame.config.SpringContext;
import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author huangwei10
 * @create 2021/5/17
 */

@Component
public class InitCaissServer implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger("InitCaissServer");


	@Override
	public void run(String... strings) throws Exception {
		/*读取配置*/
		/*创建初始化的事件*/
		Event event = Event.buildEvent("RegisterNode","preEvent");
		EventTriggerResult eventTriggerResult = event.trigger();
		if(eventTriggerResult.isSuccess()&&eventTriggerResult.isHandled()){
			for(EventHandlerResult eventHandlerResult : eventTriggerResult.getHandlerResults()){
				if(!eventHandlerResult.isSuccess()) {
					/*如果注册节点任务失败则关闭应用*/
					((ConfigurableApplicationContext)SpringContext.getApplicationContext()).close();
				}
			}
		}
	}
}
