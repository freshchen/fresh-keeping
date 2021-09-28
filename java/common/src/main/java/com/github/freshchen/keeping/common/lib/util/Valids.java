package com.github.freshchen.keeping.common.lib.util;

import com.github.freshchen.keeping.common.lib.constant.Patterns;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author darcy
 * @since 2020/02/20
 **/
public class Valids {

    /**
     * 校验 邮箱格式
     *
     * @param s
     * @return
     */
    public static boolean email(String s) {
        return StringUtils.isNoneBlank(s) && Patterns.EMAIL.matcher(s).matches();
    }

    /**
     * 校验 电话号码
     *
     * @param s
     * @return
     */
    public static boolean phone(String s) {
        return StringUtils.isNoneBlank(s) && Patterns.PHONE.matcher(s).matches();
    }

    /**
     * 校验 数字
     *
     * @param s
     * @return
     */
    public static boolean number(String s) {
        return StringUtils.isNoneBlank(s) && Patterns.NUMBER.matcher(s).matches();
    }

    /**
     * 校验 ipv4
     *
     * @param s
     * @return
     */
    public static boolean ipv4(String s) {
        return StringUtils.isNoneBlank(s) && Patterns.IPV4.matcher(s).matches();
    }

    /**
     * 校验 是否带小数位数字
     *
     * @param s
     * @return
     */
    public static boolean decimal(String s) {
        return StringUtils.isNoneBlank(s) && Patterns.DECIMAL.matcher(s).find();
    }

    /**
     * 校验 是否有空元素
     *
     * @param objects
     * @return
     */
    public static boolean containsNull(Object... objects) {
        return Stream.of(objects).anyMatch(Objects::isNull);
    }

    private Valids() {
    }
}
