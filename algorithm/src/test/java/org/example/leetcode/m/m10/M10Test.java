package org.example.leetcode.m.m10;

import org.example.leetcode.m.m1.M10;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class M10Test {
    @Test
    void test1() {
        M10 o = new M10();
        assertEquals(3,
                o.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 4}));
    }
}