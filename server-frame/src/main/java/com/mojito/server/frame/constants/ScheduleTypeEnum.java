package com.mojito.server.frame.constants;

/**
 * @author hw
 * @create 2021/5/16
 */
public enum ScheduleTypeEnum {

    COMM_SCHEDULE("comm_schedule");

    private String scheduleType;

    private ScheduleTypeEnum(String scheduleType){
        this.scheduleType = scheduleType;
    }

    public String getScheduleType() {
        return scheduleType;
    }
}
