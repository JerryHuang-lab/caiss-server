package com.mojito.server.frame.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hw
 * @create 2021/5/15
 */

public class EventContext {


    private static Map<String, Object> transferMap;

    public static Object getObject(String key){
        if(null == transferMap){
            transferMap = new HashMap<>();
            return null;
        } else if(transferMap.containsKey(key)){
            return transferMap.get(key);
        }
        return null;
    }

    public static void storeTransferData(String key, Object o){
        if(null == transferMap){
            transferMap = new HashMap<>();
        }
        transferMap.put(key,o);
    }
}
