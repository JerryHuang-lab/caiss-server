package com.mojito.server.frame.event;

import com.mojito.server.frame.annotation.Schedule;
import com.mojito.server.frame.annotation.TaskDesc;
import com.mojito.server.frame.asynctask.ScheduleCenter;
import com.mojito.server.frame.event.exception.EventException;
import com.mojito.server.frame.event.log.EventLogger;
import com.mojito.server.frame.event.result.EventHandlerResult;
import com.mojito.server.frame.event.result.EventTriggerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务事件处理器
 *
 * @author huangwei10
 * @create 2021/5/16
 */

public abstract class Task extends AbstractEventHandler {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private boolean inited = false;

    private String taskName;

    private Schedule scheduleAnno;

    private static Map<String, Task> taskMap = new HashMap<String, Task>();



    public final EventHandlerResult execute(Event event){
        init();
        if(getScheduleAnno() != null && "!schedule".equals(event.getEventSource())){
            /*如果是异步任务 则执行异步责任中心*/
            ScheduleCenter scheduleCenter = ScheduleCenter.getScheduleCenter(scheduleAnno.scheduleType());
            if(null == scheduleCenter){
                EventLogger.logScheduleCenterNotExisted(event);
                return EventHandlerResult.getExceptionResult(new EventException("scheduleCenter is not existed,scheduleType:"+scheduleAnno.scheduleType()));
            } else {
                return scheduleCenter.executeTask(event,this,scheduleAnno);
            }


        }
        EventHandlerResult handleResult = null;
        try{
            handleResult = handleTask(event);
            if(null == handleResult){
                EventLogger.logHandlerExecutedAndResultIsNull(event,this);
                return handleResult;
            } else {
                if(handleResult.isSuccess()){
                    //事件执行成功
                    EventTriggerResult innerResult = triggerTaskEvent(event, "success");
                    if(!innerResult.isSuccess()){
                        handleResult = EventHandlerResult.getFailureResult("innerEventFailure", "innerEventFailure");
                        handleResult.setInnerResult(innerResult);
                    }

                } else {
                    triggerTaskEvent(event, "failure");
                }
            }
        }catch(Exception e){
            EventLogger.logHandleTaskException(event, this, e);
            handleResult = EventHandlerResult.getExceptionResult(e);
            triggerTaskEvent(event, "failure");
        }
        triggerTaskEvent(event, "complete");
        return  handleResult;
    }


    @Override
    public void init(){
        if(!inited) {
            super.init();
            /*获取Task类的子类*/
            scheduleAnno = AnnotationUtils.getAnnotation(this.getClass(), Schedule.class);
            taskName = getTaskName();
            if(taskMap.containsKey(taskName)){
                EventLogger.logTaskNameDuplicates(taskName, this);
            }else{
                taskMap.put(taskName, this);
            }
            inited = true;
            EventLogger.logTaskInited(this);
        }
    }



    /**
     * @return
     * 获取注解任务名字
     */
    public String getTaskName(){
        //如果有注解
        if(null == taskName){
            TaskDesc taskDescAnno = AnnotationUtils.getAnnotation(this.getClass(), TaskDesc.class);
            if(null != taskDescAnno){
                this.taskName = taskDescAnno.value();
            }
        }
        //没有注解
        if(null == taskName){
            String clazzName = this.getClass().getSimpleName();
            if(clazzName.endsWith("Task")){
                taskName = clazzName.substring(0, clazzName.length()-4);
            }
        }
        return  taskName;
    }

    private Schedule getScheduleAnno(){
        if(scheduleAnno == null){
            scheduleAnno = AnnotationUtils.getAnnotation(this.getClass(), Schedule.class);
        }
        return scheduleAnno;
    }

    protected EventTriggerResult triggerTaskEvent(Event event, String subEventType){
        return triggerNextEvent(getEventTask(event, subEventType));
    }

    public boolean isDefaultRetry(){
        init();
        return scheduleAnno != null && scheduleAnno.defaultRetry();
    }



    /**
     * @param nextEvent
     * @return
     * 触发后续事件
     * eventType = "task1.success"
     */
    protected EventTriggerResult triggerNextEvent(Event nextEvent){
        return nextEvent.trigger();
    }

    /**
     * @param preEvent 前置事件
     * @param subEventType 本次事件为结果
     * @return
     */
    protected Event getEventTask(Event preEvent, String subEventType){
        //如task.success
        return Event.buildEvent(getTaskName()+"."+subEventType, preEvent);
    }

    protected  abstract  EventHandlerResult handleTask(Event event);





}
