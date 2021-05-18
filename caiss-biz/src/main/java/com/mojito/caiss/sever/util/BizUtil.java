package com.mojito.caiss.sever.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.mojito.server.frame.event.EventRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.*;

/**
 * @author huangwei10
 * @create 2019/3/15
 */

public class BizUtil {

    private static final Logger log = LoggerFactory.getLogger(BizUtil.class);


    public static boolean checkListNotEmpty(Collection<?> collection) {
        return null != collection && collection.size() > 0;
    }

}
