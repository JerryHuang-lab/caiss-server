package com.mojito.server.frame.constants;

/**
 * @author hw
 * @create 2021/5/15
 */

public class EventConstants {

    public  enum EventSourceType{
        DEFAULT_EVENT_TYPE("default"),
        PRE_EVENT_TYPE("pre");

        private String type;

        EventSourceType(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
