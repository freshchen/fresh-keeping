package org.example.leetcode;

import org.example.leetcode.s.s10.S4;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class S4Test {

    @Test
    void twoSum1() {
        S4 s4 = new S4();
        int[] a = {1, 2, 3, 4, 5};
        int[] ints = s4.twoSum(a, 9);
        assertEquals(3, ints[0]);
        assertEquals(4, ints[1]);

        int[] a1 = {1, 2, 3, 4, 5, 6};
        int[] ints1 = s4.twoSum(a1, 10);
        assertEquals(3, ints1[0]);
        assertEquals(5, ints1[1]);
    }

    @Test
    void twoSum2() {
        S4 s4 = new S4();
        int[] a1 = {1, 2, 3, 4, 5, 6};
        int[] ints1 = s4.twoSum(a1, 10);
        assertEquals(3, ints1[0]);
        assertEquals(5, ints1[1]);
    }

    @Test
    void twoSum3() {
        S4 s4 = new S4();
        int[] a1 = {0, 4, 3, 0};
        int[] ints1 = s4.twoSum(a1, 0);
        assertEquals(0, ints1[0]);
        assertEquals(3, ints1[1]);
    }

    @Test
    void twoSum4() {
        S4 s4 = new S4();
        int[] a1 = {-1,-2,-3,-4,-5};
        int[] ints1 = s4.twoSum(a1, -8);
        assertEquals(2, ints1[0]);
        assertEquals(4, ints1[1]);
    }
}