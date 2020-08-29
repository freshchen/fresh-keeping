package org.example.leetcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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