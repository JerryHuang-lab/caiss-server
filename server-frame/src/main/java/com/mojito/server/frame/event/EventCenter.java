package com.mojito.server.frame.event;

import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.event.exception.EventException;
import com.mojito.server.frame.event.handler.EventHandler;
import com.mojito.server.frame.event.handler.EventHandlerGroup;
import com.mojito.server.frame.event.log.EventLogger;
import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import com.mojito.server.frame.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件控制中心
 *
 * @author hw
 * @create 2021/5/15
 */

public class EventCenter {


	private final static Logger log = LoggerFactory.getLogger(EventCenter.class);

	private static final EventCenter EVENT_CENTER = new EventCenter();


	/**
	 * 监听事件和处理绑定
	 */
	private static final Map<String, List<EventHandlerGroup>> ROUTE_HANDLE_MAP = new ConcurrentHashMap<>();


	/**
	 * 记录最后一次触发事件
	 */
	private final static ThreadLocal<Event> lastTriggerEvent = new ThreadLocal<Event>();

	public static EventCenter getInstance() {
		return EVENT_CENTER;
	}

	public final EventTriggerResult trigger(final Event event) {
		if (null == event) {
			/*抛出异常*/
			throw new EventException("event is null");
		}
		if (event.isTriggered()) {
            /*线程不安全 重复触发事件*/
			EventLogger.logEventHasTriggered(event);
			throw new EventException("event is triggered exception");
		}
		event.setTriggered(true);
		lastTriggeredEvent(event);
		final EventTriggerResult triggerResult = new EventTriggerResult();
		final List<EventHandlerGroup> eventHandlerGroups = ROUTE_HANDLE_MAP.get(event.getEventType());
		Iterator<EventHandlerGroup> handlerGroupIterator = getHandlerIteratorGroup(
				eventHandlerGroups, event);
		while (handlerGroupIterator.hasNext()) {
			EventHandlerGroup eventHandlerGroup = handlerGroupIterator.next();
			EventHandler eventHandler = eventHandlerGroup.getEventHandler();
			EventHandlerResult handlerResult;
			try {
				EventLogger.logFindHandler(event, eventHandler);
				triggerResult.setHandled(true);
                /*新增参数校验*/
				eventHandlerGroup.lock();
				handlerResult = eventHandler.execute(event);
				eventHandlerGroup.unLock();
                    /*标记触发结果已有处理*/
				if (null != handlerResult) {
					EventLogger.logHandlerExecuted(event, eventHandler, handlerResult);
					if (handlerResult.toStopEventHandling()) {
						EventLogger.logHandlerExecutedAndStopHanding(event, eventHandler);
						break;
					} else {
						EventLogger.logHandlerExecutedAndResultIsNull(event, eventHandler);
					}
				}

			} catch (Exception e) {
				EventLogger.logHandlerExecuteException(event, eventHandler, e);
				handlerResult = EventHandlerResult.getExceptionResult(e);
			}
			triggerResult.addEventHandlerResult(handlerResult);
			event.storeEventHandlerRecord(eventHandler, handlerResult);
		}
		return triggerResult;
	}

	private static Iterator<EventHandlerGroup> getHandlerIteratorGroup(
			List<EventHandlerGroup> eventHandlerGroups, Event event) {
		return new EventHandlerGroupIterator(eventHandlerGroups, event);
	}


	private static class EventHandlerGroupIterator implements Iterator<EventHandlerGroup> {
		private List<EventHandlerGroup> eventHandlerGroups;
		private Iterator<EventHandlerGroup> groupsIterator;
		private Event event;
		private EventHandlerGroup nextEventHandlerGroup;
		private int endFlag;//-1代表迭代结束

		public EventHandlerGroupIterator(
				List<EventHandlerGroup> eventHandlerGroups, Event event) {
			this.eventHandlerGroups = eventHandlerGroups;
			this.event = event;
		}


		@Override
		public boolean hasNext() {
			if (endFlag == -1) {
				nextEventHandlerGroup = null;
				return false;
			}
			while (true) {
				if (groupsIterator == null) {
					if (eventHandlerGroups != null) {
						groupsIterator = eventHandlerGroups.iterator();
					} else {
						endFlag = -1;
						nextEventHandlerGroup = null;
						return false;
					}
				}
				if (groupsIterator.hasNext()) {
					EventHandlerGroup nextEventHandlerGroup = groupsIterator
							.next();
					if (nextEventHandlerGroup.getEventFilters() == null
							|| nextEventHandlerGroup.getEventFilters().length == 0) {
						this.nextEventHandlerGroup = nextEventHandlerGroup;
						return true;
					} else {
						//遍历每一个过滤器，这里要求每个过滤器都通过才返回，也就是AND的关系
						boolean pass = true;
						for (EventFilter filter : nextEventHandlerGroup
								.getEventFilters()) {
							if (filter == null) continue;
							try {
								if (filter.doFilter(event)) {
									if (nextEventHandlerGroup.getEventHandler() instanceof EventFilter) {
										//如果处理器本身也实现了过滤器，则用其过滤器再过滤一遍
										if (((EventFilter) nextEventHandlerGroup.getEventHandler()).doFilter(event)) {
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
							} catch (Throwable e) {
								EventLogger.logFilterExecuteException(event, filter, e);
								pass = false;
								break;
							}
						}
						if (pass) {
							this.nextEventHandlerGroup = nextEventHandlerGroup;
							return true;
						}
					}
				} else {
					return false;
				}
			}
		}

		@Override
		public EventHandlerGroup next() {
			return nextEventHandlerGroup;
		}

		@Override
		public void remove() {
			// not support
		}
	}


	/**
	 * 侦听事件
	 *
	 * @param eventType    事件类型，支持*星号通配符，支持正则表达式（/regex/），不能为空
	 * @param handler      处理器对象，不能为空
	 * @param eventFilters 过滤器，可以为空或多个，多个过滤器之间是且的关系，所有过滤器都通过才执行处理器
	 */
	public void doRegister(String eventType, EventHandler handler, Lock lock,
						   EventFilter... eventFilters) {
		if (!StringUtil.isNotEmpty(eventType)) {

		}

		EventLogger.logRegist(eventType, handler, eventFilters);

		//如果处理器本身也是过滤器，那么把过滤器添加到过滤器列表
		if (handler instanceof EventFilter) {
			eventFilters = appendFilter((EventFilter) handler, eventFilters);
		}
		//暂时支持固定的事件类型
		EventHandlerGroup eventHandlerGroup = new EventHandlerGroup(
				handler, eventFilters, lock);
		synchronized (ROUTE_HANDLE_MAP) {
			List<EventHandlerGroup> eventTupleList = ROUTE_HANDLE_MAP
					.get(eventType);
			if (eventTupleList == null) {
				eventTupleList = new ArrayList<EventHandlerGroup>();
				ROUTE_HANDLE_MAP.put(eventType, eventTupleList);
			}
			eventTupleList.add(eventHandlerGroup);
		}
	}

	private EventFilter[] appendFilter(EventFilter filter,
									   EventFilter[] eventFilters) {
		if (eventFilters == null || eventFilters.length == 0) {
			eventFilters = new EventFilter[1];
			eventFilters[0] = filter;
		} else {
			EventFilter[] oldarray = eventFilters;
			eventFilters = new EventFilter[eventFilters.length + 1];
			System.arraycopy(oldarray, 0, eventFilters, 0, oldarray.length);
			eventFilters[eventFilters.length - 1] = filter;
		}
		return eventFilters;
	}


	private void lastTriggeredEvent(Event event) {
		if (null != event) {
			lastTriggerEvent.set(event);
		}

	}

	public Event getLastTriggerEvent() {
		return lastTriggerEvent.get();
	}
}
