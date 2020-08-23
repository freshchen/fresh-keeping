package org.example.leetcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class A3Test {

    @Test
    void isValid() {
        A3 a3 = new A3();
        assertTrue(a3.isValid("(){}[]"));
        assertFalse(a3.isValid("([)]"));
        assertTrue(a3.isValid("(({[]}))"));
        assertFalse(a3.isValid("]"));
        assertFalse(a3.isValid("["));
    }
}