package com.mojito.server.frame.event;

/**
 * @author hw
 * @create 2021/5/15
 */

public interface EventFilter {

    boolean doFilter(Event event);
}
