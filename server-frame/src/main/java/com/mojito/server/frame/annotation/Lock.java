package com.mojito.server.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hw
 * @create 2021/5/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Lock {


    /**
     * 锁名称
     * @return
     */
    String lockName() default "";

    /**
     * 锁类型 分布式锁，读写锁，自旋锁，普通锁
     * @return
     */
    String lockType() default "local";

    /**
     * 锁的key, 根据key来缩小锁的范围，命中把锁
     * @return
     */
    String lockKey() default "";

    /**
     * lockKey的匹配，0代表新生成一个lock，相同的lockKey用同一把锁
     * @return
     */
    int lockMatch() default 0;

    long lockTime() default 0;


}
