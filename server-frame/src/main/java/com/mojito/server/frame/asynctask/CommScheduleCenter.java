package com.mojito.server.frame.asynctask;

import com.mojito.server.frame.annotation.Schedule;
import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.Task;
import com.mojito.server.frame.event.exception.EventException;
import com.mojito.server.frame.event.log.EventLogger;
import com.mojito.server.frame.event.result.EventHandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IO交互异步关联线程 将事件的uuid存储在SCHEDULE_CENTER
 *
 * @author huangwei10
 * @create 2017/9/7
 */

public class CommScheduleCenter extends  ScheduleCenter {

    private static  final Logger log = LoggerFactory.getLogger(CommScheduleCenter.class);

    private static ScheduleCenter instance;

    public static ScheduleCenter getInstance(){
        if(instance == null){
            instance = new CommScheduleCenter();
        }
        return instance;
    }


    private static final Map<String, Event> COMM_SCHEDULE_CENTER = new ConcurrentHashMap<String, Event>();
    /**
     * 异步执行
     * @param task
     * @param asyncAnno
     * @return
     */

    @Override
    public EventHandlerResult executeTask(final  Event event, final Task task, Schedule asyncAnno) {
        final Event scheduleEvent = Event.buildEvent(event.getEventType(), "Schedule");

        for(String keyName : asyncAnno.value()){
            Object value = event.getEventContext().getObject(keyName);
            if(null != value){
                scheduleEvent.getEventContext().storeTransferData(keyName, value.toString());
            }
        }
        /*存储异步事件*/
        storeAsyncEvent(event);
        final AtomicInteger retryCount = new AtomicInteger();
        try{
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    EventHandlerResult result = task.execute(scheduleEvent);
                    if(null == result){
                        EventLogger.logHandlerExecutedAndResultIsNull(scheduleEvent,task);
                    } else {
                        if(!result.isSuccess()){
                            boolean needretry = result.isNeedRetry() | task.isDefaultRetry();
                            EventLogger.logScheduleTaskExecuted(scheduleEvent, task, result, needretry);
                            if(!needretry){
                                timer.cancel();
                            } else if(retryCount.incrementAndGet() > 3){
                                EventLogger.logScheduleWillTermination(scheduleEvent, task);
                                timer.cancel();
                            }
                        } else {
                            timer.cancel();
                        }
                    }
                    EventLogger.logHandlerTrace(task, result);
                }
            };
            long delay = asyncAnno.delay();
            timer.schedule(timerTask, delay, 10000L);
        }catch(Exception e){
            EventHandlerResult.getExceptionResult(e);
        }
        return EventHandlerResult.getSuccessResult();
    }


    private void storeAsyncEvent(Event asyncEvent){
        if(COMM_SCHEDULE_CENTER.containsKey(asyncEvent.getEventTraceUUid())){
            EventLogger.logEventTraceUUidRepeated(asyncEvent);
        } else {
            COMM_SCHEDULE_CENTER.put(asyncEvent.getEventTraceUUid(), asyncEvent);
        }
    }

    /**
     *
     * 此方法回调的时候获取异步事件的上次事件标识
     * @param eventTraceUUid
     * @return
     */
    public static Event getAsyncCommEvent(String eventTraceUUid){
        if(!COMM_SCHEDULE_CENTER.containsKey(eventTraceUUid)){
            /*抛出异常*/
            throw new EventException("eventTraceUUid is not existed, eventTraceUUid = "+eventTraceUUid);
        } else {
            return COMM_SCHEDULE_CENTER.get(eventTraceUUid);
        }
    }
}
