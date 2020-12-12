package com.github.freshchen.keeping.keeping.leetcode.m.m11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class M11Test {

    @Test
    void test1() {
        M11 o = new M11();
        int[][] a = {
                {1, 5, 9},
                {10, 11, 13},
                {12, 13, 15}
        };
        Assertions.assertEquals(12, o.kthSmallest(a, 6));
    }

}
