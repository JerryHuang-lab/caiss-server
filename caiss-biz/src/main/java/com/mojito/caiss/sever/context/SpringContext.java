package com.mojito.caiss.sever.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangwei10
 * @create 2021/6/29
 */
@Configuration
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
