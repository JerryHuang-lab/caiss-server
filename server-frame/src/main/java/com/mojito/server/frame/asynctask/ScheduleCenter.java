package com.mojito.server.frame.asynctask;


import com.mojito.server.frame.annotation.Schedule;
import com.mojito.server.frame.constants.ScheduleTypeEnum;
import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.Task;
import com.mojito.server.frame.event.result.EventHandlerResult;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步任务调度器
 *
 * @author huangwei10
 * @create 2021/5/16
 */

public abstract class ScheduleCenter {



    /**
     * 存储调度中心
     */
    private static final Map<String, ScheduleCenter> SCHEDULE_CENTER_MAP = new ConcurrentHashMap<>();

    static {
        SCHEDULE_CENTER_MAP.put(ScheduleTypeEnum.COMM_SCHEDULE.getScheduleType(), CommScheduleCenter.getInstance());
    }


    public abstract EventHandlerResult executeTask(Event event, Task task, Schedule scheduleAnno);


    public static ScheduleCenter getScheduleCenter(String scheduleType) {
        if(!StringUtils.hasText(scheduleType)){
            return null;
        }
        if(SCHEDULE_CENTER_MAP.containsKey(scheduleType)) {
            return SCHEDULE_CENTER_MAP.get(scheduleType);
        }
        return null;
    }

}
