package com.github.freshchen.keeping.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author darcy
 * @since 2020/08/09
 **/
@Builder
@Getter
@ToString
public class HttpRequestLog {

    // 协议
    private String protocol;

    // 请求地址
    private String url;

    // 请求方法
    private String method;

    // 请求体
    private String body;


    // 请求头
    @Singular("header")
    private List<String> headers = newArrayList();
}
