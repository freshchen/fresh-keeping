package com.github.freshchen.keeping.constant;

import java.util.regex.Pattern;

/**
 * @author darcy
 * @since 2020/02/20
 **/
public class Patterns {

    public static final Pattern PHONE = Pattern.compile("\\d{11}");

    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_\\.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    public static final Pattern NUMBER = Pattern.compile("[0-9]+");

    public static final Pattern DECIMAL = Pattern.compile("-?[0-9]+\\.[0-9]+");

    private static final String LOWER = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
    public static final Pattern IPV4 = Pattern.compile(LOWER + "(\\." + LOWER + "){3}");

    public static final Pattern ZERO_END_OF_DECIMAL = Pattern.compile("\\.{1}0+$|0+$");

    private Patterns() {
    }
}
