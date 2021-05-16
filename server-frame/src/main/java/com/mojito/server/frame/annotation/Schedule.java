package com.mojito.server.frame.annotation;/**
 * 异步任务注解
 *
 * @author huangwei10
 * @create 2017/9/7
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huangwei10 on 2017/9/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Schedule {


    String scheduleType() default "";

    String[] value() default {};
    /**
     * 调度中心的任务名称
     * 修改这个值将影响调度中心的回调
     * 一般情况下不要手动设置
     *
     * @return
     */
    String taskType() default "";

    /**
     * 任务代码，默认为任务名称
     * 一般情况下不推荐手动设置
     *
     * @return
     */
    String subTaskCode() default "";

    /**
     * 任务描述，将体现在调度中心
     * 如果任务描述不是常量，则需要任务实现TaskUuid接口，优先于本参数
     *
     * @return
     */
    String taskDesc() default "";

    /**
     * 是否默认重试
     * 默认为否，由返回结果中的needRetry决定是否需要重试
     * 如果设为是，则无论什么情况下只要失败都重试
     *
     * @returnd
     */
    boolean defaultRetry() default false;

    /**
     * 调度延时的毫秒数
     * 默认为0
     * 为0或负数则表示不延时
     *
     * @return
     */
    long delay() default 0L;

    /**
     * 任务是否可重复的
     * 默认为false，通过uuid去除
     * 如果不希望去重，则设置为true
     * @return
     */
    boolean repeatable() default false;
}
