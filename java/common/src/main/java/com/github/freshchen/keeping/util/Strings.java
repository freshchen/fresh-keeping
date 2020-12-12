package com.github.freshchen.keeping.util;

import com.github.freshchen.keeping.constant.Patterns;
import org.apache.commons.lang3.StringUtils;

/**
 * @author darcy
 * @since 2020/02/17
 **/
public class Strings {

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public static String capitalizeFirstLetter(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return String.valueOf(chars);
        }
        return s;
    }

    /**
     * 去除字符串后面的小数点和0
     * "2.00" -> "2"
     * "2.100" -> "2.1"
     * "2000" -> "2000"
     *
     * @param s 需要转换的数字
     * @return 转换后的数字
     */
    public static String trimZeroEndOfDecimal(String s) {
        if (StringUtils.isNotBlank(s) && Valids.decimal(s)) {
            return Patterns.ZERO_END_OF_DECIMAL.matcher(s).replaceAll(StringUtils.EMPTY);
        }
        return s;
    }

    private Strings() {
    }
}
