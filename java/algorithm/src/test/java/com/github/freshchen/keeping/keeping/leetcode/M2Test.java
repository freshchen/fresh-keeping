package com.github.freshchen.keeping.keeping.leetcode;

import com.github.freshchen.keeping.keeping.leetcode.m.m1.M2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class M2Test {

    @Test
    void reset() {
    }

    @Test
    void shuffle() {
        int[] ints = {1, 2, 3};
        M2 m2 = new M2(ints);
        int[] shuffle = m2.shuffle();
        Arrays.stream(shuffle)
                .forEach(System.out::println);
    }
}
