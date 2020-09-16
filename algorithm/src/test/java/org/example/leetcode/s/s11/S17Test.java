package org.example.leetcode.s.s11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S17Test {
    @Test
    void test1() {
        S17 s17 = new S17();
        Assertions.assertEquals(0, s17.firstUniqChar("leetcode"));
    }
}