package com.mojito.server.frame.event.handler;

import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.event.EventFilter;
import com.mojito.server.frame.lock.LockSupportCache;
import lombok.Data;

/**
 * @author hw
 * @create 2021/5/15
 */
@Data
public class EventHandlerGroup {

    private EventHandler  eventHandler;


    private EventFilter[] eventFilters;

    private Lock lock;

    public EventHandlerGroup(EventHandler eventHandler, EventFilter[] eventFilters, Lock lock){
        this.eventFilters = eventFilters;
        this.eventHandler =eventHandler;
        this.lock = lock;
    }

    public void lock() throws Exception {
        if(null != lock){
            LockSupportCache.getLock(lock).lock(lock);
        }
    }

    public void unLock() throws Exception {
        if(null != lock){
            LockSupportCache.getLock(lock).unLock(lock);
        }
    }
}
