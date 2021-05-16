package com.mojito.server.frame.event.handler;

import com.mojito.server.frame.event.EventFilter;
import lombok.Data;

/**
 * @author hw
 * @create 2021/5/15
 */
@Data
public class EventHandlerGroup {

    private EventHandler  eventHandler;


    private EventFilter[] eventFilters;

    public EventHandlerGroup(EventHandler eventHandler, EventFilter[] eventFilters){
        this.eventFilters = eventFilters;
        this.eventHandler =eventHandler;
    }

}
