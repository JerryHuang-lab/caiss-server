package com.mojito.cluster.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
