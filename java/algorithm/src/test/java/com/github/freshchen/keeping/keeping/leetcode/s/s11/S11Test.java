package com.github.freshchen.keeping.keeping.leetcode.s.s11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class S11Test {
    @Test
    void test1() {
        S11 s11 = new S11();
        Assertions.assertEquals(0, s11.singleNumber(new int[]{2, 0, 2, 1, 1}));
    }
}
