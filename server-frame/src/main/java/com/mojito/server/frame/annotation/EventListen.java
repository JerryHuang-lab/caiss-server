package com.mojito.server.frame.annotation;/**
 * 事件监听
 *
 * @author huangwei10
 * @create 2021/05/16
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huangwei10 on 2017/8/23.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventListen {
    //过滤列表
    String[] value();
}
