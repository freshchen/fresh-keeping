package com.github.freshchen.keeping.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author lingchen02
 * @since 2021/11/18
 */
public final class TraceIdContextHolder {

    public static void initTraceId() {
        UUID uuid = UUID.randomUUID();
        String tid = Long.toString(Math.abs(uuid.getMostSignificantBits() + uuid.getLeastSignificantBits()));
        MDC.put("tid", tid);
    }

    private TraceIdContextHolder() {
    }
}
