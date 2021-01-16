package com.github.freshchen.keeping;

import com.google.common.collect.Maps;
import feign.Request;
import feign.Response;
import feign.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * 生产中用项目 json 库代替 map
 *
 * @author freshchen
 */
public class FreshFeignLogger extends feign.Logger {

    /**
     * Feign日志用的字段
     */
    final String LOG_TYPE_KEY = "Type of log";
    final String LOG_TYPE_REQUEST = "request";
    final String LOG_TYPE_DEFAULT = "default";
    final String LOG_TYPE_RESPONSE = "response";
    final String CLIENT_NAME = "Client";
    final String HEADS = "Head";
    final String METHOD = "Method";
    final String URL = "Url";
    final String BODY = "Body";
    final String STATUS = "Status";
    final String ELAPSED_TIME = "Elapsed";
    final String ARGS = "Args";
    final String FORMAT = "Format";

    private Logger log;

    public FreshFeignLogger() {
        this(feign.Logger.class);
    }

    public FreshFeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public FreshFeignLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    FreshFeignLogger(Logger logger) {
        this.log = logger;
    }


    @Override
    protected void log(String configKey, String format, Object... args) {
        Map<String, Object> object = Maps.newHashMap();
        object.put(CLIENT_NAME, configKey);
        object.put(FORMAT, format);
        object.put(ARGS, args);
        writeLog(object, configKey, LOG_TYPE_DEFAULT);
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        Map<String, Object> object = Maps.newHashMap();
        object.put(LOG_TYPE_KEY, LOG_TYPE_REQUEST);
        object.put(CLIENT_NAME, configKey);
        object.put(HEADS, request.headers());
        object.put(METHOD, request.httpMethod());
        object.put(URL, request.url());
        try {
            object.put(BODY, request.body());
        } catch (Exception e) {
            object.put(BODY, request.body());
        }
        writeLog(object, configKey, LOG_TYPE_REQUEST);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey,
                                              Level logLevel,
                                              Response response,
                                              long elapsedTime)
            throws IOException {
        Map<String, Object> object = Maps.newHashMap();
        object.put(LOG_TYPE_KEY, LOG_TYPE_RESPONSE);
        object.put(URL, response.request().url());
        object.put(CLIENT_NAME, configKey);
        object.put(HEADS, response.headers());
        object.put(STATUS, response.status());
        object.put(ELAPSED_TIME, elapsedTime);
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        try {
            object.put(BODY, new String(bodyData));
        } catch (Exception e) {
            object.put(BODY, new String(bodyData));
        }
        writeLog(object, configKey, LOG_TYPE_RESPONSE);
        return response.toBuilder().body(bodyData).build();
    }

    private void writeLog(Map<String, Object> object, String configKey, String type) {
        log.info("<--- FeignLog {} {} begin {} {} {} end --->",
                configKey, type, object.toString(), configKey, type);
    }
}
