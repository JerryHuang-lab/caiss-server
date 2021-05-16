package com.mojito.server.frame.lock.handler;

import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.config.SpringContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author hw
 * @create 2021/5/16
 */

public class DistributeLockHandler implements LockHandler{


    private RLock rLock = null;

    public DistributeLockHandler(Lock lock){
        RedissonClient redissonClient = (RedissonClient) SpringContext.getApplicationContext().getBean("redissonClient");
        rLock = redissonClient.getLock(lock.lockKey());
    }

    @Override
    public void lock(Lock lock) {
        if(lock.lockTime() == 0){
            rLock.lock();
        } else {
            rLock.lock(lock.lockTime(), TimeUnit.SECONDS);
        }

    }

    @Override
    public void unLock(Lock lock) {
       if(rLock.isLocked()){
           rLock.unlock();
       }
    }
}
