package com.github.freshchen.keeping.keeping.introduction.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class HeapTest {

    @Test
    void sort() {
        int[] nums = new int[]{13, 21, 4, 5, 6, 88, -1};
        System.out.println(Arrays.toString(nums));
        Heap.sort(nums);
        System.out.println(Arrays.toString(nums));
    }

}
