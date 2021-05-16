package com.mojito.server.frame.lock.handler;

import com.mojito.server.frame.annotation.Lock;

/**
 * @author hw
 * @create 2021/5/16
 */

public interface LockHandler {


    /**
     * 实际上执行锁
     */
    void lock(Lock lock);

    /**
     * 释放锁
     * @return
     */
    void unLock(Lock lock);
}
