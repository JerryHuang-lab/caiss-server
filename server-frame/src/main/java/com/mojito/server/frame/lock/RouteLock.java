
package com.mojito.server.frame.lock;

import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.lock.handler.DistributeLockHandler;
import com.mojito.server.frame.lock.handler.LockHandler;
import com.mojito.server.frame.lock.handler.ReentantLockHandler;


/**
 * @author hw
 * @create 2021/5/16
 */

public class RouteLock{

    private volatile static  RouteLock routeLock;

    private RouteLock(){
    }



    public static RouteLock getInstance(){
        if(null != routeLock){
            return routeLock;
        }
        synchronized (RouteLock.class){
            if(null == routeLock){
                routeLock = new RouteLock();
            }
        }
        return routeLock;
    }


    public LockHandler getLockHandler(Lock lock){
        if(lock.lockType().equalsIgnoreCase("lock")){
            return new ReentantLockHandler();
        } else if(lock.lockType().equalsIgnoreCase("dis_lock")){
            return new DistributeLockHandler(lock);
        }
        return null;
    }


}
