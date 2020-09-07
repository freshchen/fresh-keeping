package org.example.leetcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class M5Test {

    @Test
    void isValid() {
        M5 m5 = new M5();
        int i = m5.canCompleteCircuit(
                new int[]{0, 0, 10},
                new int[]{2, 4, 4}
        );
        Assertions.assertEquals(2, i);
        System.out.println(3 >> 1);
    }
}