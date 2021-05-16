package com.mojito.server.frame.event;

import com.mojito.server.frame.constants.EventConstants;
import com.mojito.server.frame.context.EventContext;
import com.mojito.server.frame.event.handler.EventHandler;
import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件基础类型
 * @author hw
 * @create 2021/5/15
 */
@Data
public class Event {

    /**
     * 事件追踪的唯一id， 用于异步事件回溯
     */
    private String eventTraceUUid;

    /**
     * 事件来源
     */
    private String eventSource;

    /**
     * 事件类型 区分不同模块
     */
    private String eventType;

    /**
     * 前置事件
     */
    private Event preEvent;

    /**
     * 事件绑定所有的处理结果
     */
    private Map<EventHandler,EventHandlerResult> handlerResultMap;


    /**
     * 事件之间传输数据存储
     */
    private EventContext eventContext;


    private boolean triggered;


    public static Event buildEvent(Event event, String eventType) {
        return buildEvent(event, eventType, EventConstants.EventSourceType.DEFAULT_EVENT_TYPE.getType());
    }

    public static Event buildEvent(Event event, String eventType, String eventSource) {
        event.eventType = eventType;
        event.eventSource = eventSource;
        return event;
    }

    public static Event buildEvent(String eventType, String eventSource) {
        return buildEvent(new Event(), eventType, eventSource);
    }

    /**
     * @param eventType
     * @param preEvent
     * @return
     * 组装监听事件
     */
    public static Event buildEvent(String eventType, Event preEvent) {
        Event event = buildEvent(eventType, EventConstants.EventSourceType.PRE_EVENT_TYPE.getType());
        event.preEvent = preEvent;
        event.eventContext = preEvent.eventContext;
        event.eventTraceUUid = preEvent.eventTraceUUid;
        return event;
    }


    public EventTriggerResult trigger(){
        EventCenter eventCenter = EventCenter.getInstance();
        if(null == eventCenter){
            eventCenter = new EventCenter();
        }
        return eventCenter.trigger(this);
    }


    public void storeEventHandlerRecord(EventHandler eventHandler, EventHandlerResult eventHandlerResult){
        if(null == handlerResultMap){
            handlerResultMap = new HashMap<>();
        }
        handlerResultMap.put(eventHandler,eventHandlerResult);
    }


    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
