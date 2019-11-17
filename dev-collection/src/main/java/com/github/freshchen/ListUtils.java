package com.github.freshchen;

import java.util.List;

/**
 * @program: dev-tools
 * @Date: 2019/11/17 22:19
 * @Author: Ling Chen
 * @Description:
 */
public interface ListUtils {

    static void print(List list) {
        list.stream().forEach(System.out::print);
    }
}
