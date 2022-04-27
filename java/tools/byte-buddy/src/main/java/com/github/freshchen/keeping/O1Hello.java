package com.github.freshchen.keeping;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author darcy
 * @since 2022/2/12
 */
public class O1Hello {

    @RuntimeType
    public static String hello(@This O1 o1) {
        return "hello !" + o1.getName();
    }

}
