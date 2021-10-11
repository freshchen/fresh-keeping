package com.github.freshchen.keeping.strategy;

/**
 * @author darcy
 * @since 2021/10/11
 */
public interface UrlHandler {

    /**
     * 获取可以处理的URL
     */
    String getUrl();

    /**
     * 处理逻辑
     */
    void handle();
}
