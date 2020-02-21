package util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringsTest {

    @Test
    public void capitalizeFirstLetter() {
        String alone = Strings.capitalizeFirstLetter("alone");
        Assert.assertEquals("Alone", alone);
    }

    @Test
    public void trimZeroEndOfDecimal() {
        String s = Strings.trimZeroEndOfDecimal("2.300");
        Assert.assertEquals("2.3", s);
    }

    @Test
    public void trimZeroEndOfDecimal1() {
        String s = Strings.trimZeroEndOfDecimal("2300");
        Assert.assertEquals("2300", s);
    }
}