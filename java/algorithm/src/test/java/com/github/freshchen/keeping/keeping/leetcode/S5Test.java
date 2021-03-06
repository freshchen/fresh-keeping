package com.github.freshchen.keeping.keeping.leetcode;

import com.github.freshchen.keeping.keeping.leetcode.s.s1.S5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class S5Test {

    @Test
    void sum() {
        S5 s5 = new S5();
        Assertions.assertEquals(3, s5.getSum(1, 2));
        Assertions.assertEquals(1, s5.getSum(1, 0));
        Assertions.assertEquals(1, s5.getSum(-1, 2));
        Assertions.assertEquals(2, s5.getSum(0, 2));
    }

}
