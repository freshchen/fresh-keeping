package org.example.leetcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class H2Test {
    @Test
    void test1() {
        H2 o = new H2();
        Assertions.assertEquals(3,
                o.firstMissingPositive(new int[]{1, 2, 0}));
    }

    @Test
    void test2() {
        H2 o = new H2();
        Assertions.assertEquals(2,
                o.firstMissingPositive(new int[]{3, 4, -1, 1}));
    }

    @Test
    void test3() {
        H2 o = new H2();
        Assertions.assertEquals(1,
                o.firstMissingPositive(new int[]{7, 8, 9, 11, 12}));
    }

    @Test
    void test4() {
        H2 o = new H2();
        Assertions.assertEquals(1,
                o.firstMissingPositive(new int[]{-1, 0}));
    }

    @Test
    void test5() {
        H2 o = new H2();
        Assertions.assertEquals(3,
                o.firstMissingPositive(new int[]{0, 2, 2, 1, 1}));
    }
}