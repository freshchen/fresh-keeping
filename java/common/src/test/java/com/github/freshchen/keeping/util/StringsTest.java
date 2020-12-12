package com.github.freshchen.keeping.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringsTest {

    @Test
    public void capitalizeFirstLetter() {
        String alone = Strings.capitalizeFirstLetter("alone");
        Assertions.assertEquals("Alone", alone);
    }

    @Test
    public void trimZeroEndOfDecimal() {
        String s = Strings.trimZeroEndOfDecimal("2.300");
        Assertions.assertEquals("2.3", s);
    }

    @Test
    public void trimZeroEndOfDecimal1() {
        String s = Strings.trimZeroEndOfDecimal("2300");
        Assertions.assertEquals("2300", s);
    }
}
