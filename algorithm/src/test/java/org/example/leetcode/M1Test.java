package org.example.leetcode;

import org.example.leetcode.m.m1.M1;
import org.junit.jupiter.api.Test;

class M1Test {

    @Test
    void setZeroes() {
        int[][] matrix = {
                {
                        1, 3, 5, 7
                },
                {
                        9, 0, 13, 15
                },
                {
                        17, 0, 21, 23
                }

        };
        M1 m1 = new M1();
        m1.setZeroes(matrix);
        int[][] result = {
                {
                        1, 0, 5, 7
                },
                {
                        0, 0, 0, 0
                },
                {
                        0, 0, 0, 0
                }

        };
    }
}