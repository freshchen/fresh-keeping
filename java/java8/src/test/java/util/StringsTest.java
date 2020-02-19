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
}