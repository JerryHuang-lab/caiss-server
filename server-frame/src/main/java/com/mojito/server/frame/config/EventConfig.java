package com.mojito.server.frame.config;

import com.mojito.server.frame.event.EventRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangwei10
 * @create 2021/5/17
 */
@Configuration
public class EventConfig {


	@Bean(value = "eventRegister",initMethod = "init")
	public EventRegister createEventRegister(){
		return new EventRegister();
	}
}
