package com.mojito.server.frame.lock;

import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.lock.handler.LockHandler;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hw
 * @create 2021/5/16
 */

public class LockSupportCache {

    private static final Map<String,LockHandler> LOCK_MAP = new ConcurrentHashMap<>();

    private static final Object atomLock = new Object();


    public static LockHandler getLock(Lock lock){
        LockHandler lockHandler = null;
        if(lock.lockType().endsWith("lock")){
            String lockKey = null;
            if (!StringUtils.hasText(lock.lockKey())){
                lockKey = "defaultLock";
            } else {
                lockKey = lock.lockKey();
            }
            /*用默认的锁*/
            if (LOCK_MAP.containsKey(lockKey)){
                lockHandler = LOCK_MAP.get(lockKey);
            } else {
                synchronized (atomLock) {
                    lockHandler = RouteLock.getInstance().getLockHandler(lock);
                    LOCK_MAP.put(lockKey, lockHandler);
                }
            }

        }
        return lockHandler;
    }

}
