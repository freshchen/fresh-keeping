package com.github.freshchen.keeping.context;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author darcy
 * @since 2021/11/18
 */
public final class TraceContextHolder {

    private TraceContextHolder() {
    }

    public static final String TRACE_ID = "traceId";

    public static String genTraceId() {
        UUID uuid = UUID.randomUUID();
        return Long.toString(Math.abs(uuid.getMostSignificantBits() + uuid.getLeastSignificantBits()));
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static void clear() {
        MDC.clear();
    }
}
