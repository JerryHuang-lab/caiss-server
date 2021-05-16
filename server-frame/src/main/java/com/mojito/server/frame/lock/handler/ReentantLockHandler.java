package com.mojito.server.frame.lock.handler;

import com.mojito.server.frame.annotation.Lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hw
 * @create 2021/5/16
 */

public class ReentantLockHandler implements LockHandler {

    private ReentrantLock reentrantLock;


    public ReentantLockHandler(){
        this.reentrantLock = new ReentrantLock();
    }

    @Override
    public void lock(Lock lock) {
        reentrantLock.lock();
    }

    @Override
    public void unLock(Lock lock) {
        if(reentrantLock.isLocked()){
            reentrantLock.unlock();
        }
    }
}
