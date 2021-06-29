package com.mojito.server.frame.event.handler;

import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.result.EventHandlerResult;

/**
 * @author hw
 * @create 2021/5/15
 */

public interface EventHandler {



    /**
     * @param event
     * @return
     * 执行事件
     */
    EventHandlerResult execute(Event event);
}
