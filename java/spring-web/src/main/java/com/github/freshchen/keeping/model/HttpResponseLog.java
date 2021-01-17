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
@Getter
@Builder
@ToString
public class HttpResponseLog {

    // 响应码
    private Integer code;

    // 响应地址
    private String url;

    // 响应体
    private String body;


    // 响应头
    @Singular("header")
    private List<String> headers = newArrayList();
}
