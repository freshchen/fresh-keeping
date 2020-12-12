package com.github.freshchen.keeping.keeping.leetcode;

import com.github.freshchen.keeping.keeping.leetcode.s.s1.S3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class S3Test {

    @Test
    void isValid() {
        S3 a3 = new S3();
        assertTrue(a3.isValid("(){}[]"));
        assertFalse(a3.isValid("([)]"));
        assertTrue(a3.isValid("(({[]}))"));
        assertFalse(a3.isValid("]"));
        assertFalse(a3.isValid("["));
    }
}
