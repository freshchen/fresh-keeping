package com.github.freshchen.keeping;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;

/**
 * @author darcy
 * @since 2022/03/20
 **/
public class TimeSpanHandler extends SpanHandler {

    @Override
    public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        // 如果我们只需要执行时间超过 5 秒的跨度
        if (span.finishTimestamp() - span.startTimestamp() > 5000){
            return true;
        }
        return false;
    }
}
