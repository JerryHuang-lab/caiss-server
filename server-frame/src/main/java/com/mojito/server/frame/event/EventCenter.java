package com.mojito.server.frame.event;

import com.mojito.server.frame.event.exception.EventException;
import com.mojito.server.frame.event.handler.EventHandler;
import com.mojito.server.frame.event.handler.EventHandlerGroup;
import com.mojito.server.frame.event.log.EventLogger;
import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件控制中心
 * @author hw
 * @create 2021/5/15
 */

public class EventCenter {


    private final static Logger log = LoggerFactory.getLogger(EventCenter.class);

    private static final EventCenter EVENT_CENTER = new EventCenter();


    /**
     * 监听事件和处理绑定
     */
    private static final Map<String,List<EventHandlerGroup>> ROUTE_HANDLE_MAP = new ConcurrentHashMap<>();


    /**
     * 记录最后一次触发事件
     */
    private final static ThreadLocal<Event> lastTriggerEvent = new ThreadLocal<Event>();

    public static EventCenter getInstance(){
        return EVENT_CENTER;
    }

    public final EventTriggerResult trigger(final Event event){
        if(null == event){
            /*抛出异常*/
            throw new EventException("event is null");
        }
        if (event.isTriggered()){
            /*重复触发事件*/
            EventLogger.logEventHasTriggered(event);
            throw new EventException("event is triggered exception");
        }
        event.setTriggered(true);
        lastTriggeredEvent(event);
        final EventTriggerResult triggerResult = new EventTriggerResult();
        final List<EventHandlerGroup> eventHandlerGroups = ROUTE_HANDLE_MAP.get(event.getEventType());
        Iterator<EventHandler> handlerIterator = getHandlerIterator(
                eventHandlerGroups, event);
        while(handlerIterator.hasNext()){
            EventHandler eventHandler =  handlerIterator.next();
            EventHandlerResult handlerResult;
            try{
                EventLogger.logFindHandler(event, eventHandler);
                handlerResult = eventHandler.execute(event);
                /*标记触发结果已有处理*/
                triggerResult.setHandled(true);
                if (null != handlerResult){
                    EventLogger.logHandlerExecuted(event,eventHandler,handlerResult);
                    if(handlerResult.toStopEventHandling()){
                        EventLogger.logHandlerExecutedAndStopHanding(event, eventHandler);
                        break;
                    } else {
                        EventLogger.logHandlerExecutedAndResultIsNull(event, eventHandler);
                    }
                }
            }catch (Exception e){
                EventLogger.logHandlerExecuteException(event, eventHandler, e);
                handlerResult = EventHandlerResult.getExceptionResult(e);
            }
            triggerResult.addEventHandlerResult(handlerResult);
            event.storeEventHandlerRecord(eventHandler, handlerResult);
        }
        return triggerResult;
    }

    private static Iterator<EventHandler> getHandlerIterator(
            List<EventHandlerGroup> eventHandlerTupleList,  Event event) {
        return new EventHandlerIterator(eventHandlerTupleList, event);
    }


    private static class EventHandlerIterator implements Iterator<EventHandler> {
        private List<EventHandlerGroup> eventHandlerGroups;
        private Iterator<EventHandlerGroup> groupsIterator;
        private Event event;
        private EventHandler nextEventHandler;
        private int endFlag;//-1代表迭代结束

        public EventHandlerIterator(
                List<EventHandlerGroup> eventHandlerGroups, Event event) {
            this.eventHandlerGroups = eventHandlerGroups;
            this.event = event;
        }


        @Override
        public boolean hasNext() {
            if (endFlag == -1) {
                nextEventHandler = null;
                return false;
            }
            while (true) {
                if (groupsIterator == null) {
                    if (eventHandlerGroups != null) {
                        groupsIterator = eventHandlerGroups.iterator();
                    } else {
                        endFlag = -1;
                        nextEventHandler = null;
                        return false;
                    }
                }
                if (groupsIterator.hasNext()) {
                    EventHandlerGroup nextEventHandlerGroup = groupsIterator
                            .next();
                    if (nextEventHandlerGroup.getEventFilters() == null
                            || nextEventHandlerGroup.getEventFilters().length == 0) {
                        nextEventHandler = nextEventHandlerGroup.getEventHandler();
                        return true;
                    } else {
                        //遍历每一个过滤器，这里要求每个过滤器都通过才返回，也就是AND的关系
                        boolean pass = true;
                        for (EventFilter filter : nextEventHandlerGroup
                                .getEventFilters()) {
                            if(filter == null) continue;
                            try{
                                if (filter.doFilter(event)) {
									if(nextEventHandler instanceof EventFilter){
										//如果处理器本身也实现了过滤器，则用其过滤器再过滤一遍
										if(((EventFilter)nextEventHandler).doFilter(event)){
											continue;
										} else {
											pass = false;
											break;
										}
									} else {
										continue;
									}
                                } else {
                                    pass = false;
                                    break;
                                }
                            } catch (Throwable e){
                                EventLogger.logFilterExecuteException(event, filter, e);
                                pass = false;
                                break;
                            }
                        }
                        if(pass){
                            nextEventHandler = nextEventHandlerGroup.getEventHandler();
                            return true;
                        }
                    }
                } else {
                   return  false;
                }
            }
        }

        @Override
        public EventHandler next() {
            return nextEventHandler;
        }

        @Override
        public void remove() {
            // not support
        }
    }



    public void register(){

    }



    private void lastTriggeredEvent(Event event){
        if(null != event){
            lastTriggerEvent.set(event);
        }

    }

    public Event getLastTriggerEvent(){
        return lastTriggerEvent.get();
    }
}
