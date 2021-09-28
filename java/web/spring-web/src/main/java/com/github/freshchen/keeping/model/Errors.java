package com.github.freshchen.keeping.model;

import com.github.freshchen.keeping.common.lib.model.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2020/06/10
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Errors {

    public static final Error SERVER_ERROR = Error.of(1, "服务异常");
    public static final Error NOT_FOUND = Error.of(2, "资源不存在");
    public static final Error UNAUTHORIZED = Error.of(3, "未授权");
    public static final Error FORBIDDEN = Error.of(4, "拒绝访问");
    public static final Error BAD_PARAMS = Error.of(5, "请求参数错误");
}
