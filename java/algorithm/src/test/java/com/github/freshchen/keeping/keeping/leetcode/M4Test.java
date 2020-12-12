package com.github.freshchen.keeping.keeping.leetcode;

import com.google.common.collect.Lists;
import com.github.freshchen.keeping.keeping.leetcode.m.m1.M4;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class M4Test {

    @Test
    void wordBreak1() {
        M4 m4 = new M4();
        boolean b = m4.wordBreak("leetcode",
                Lists.newArrayList("leet", "code"));
        Assertions.assertTrue(b);
    }
}
