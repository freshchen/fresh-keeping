package com.github.freshchen.keeping.keeping.introduction.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MergeTest {

    @Test
    void sort() {
        int[] nums = new int[]{13, 21, 4, 5, 6};
        System.out.println(Arrays.toString(nums));
        Merge.sort(nums);
        System.out.println(Arrays.toString(nums));
    }
}
