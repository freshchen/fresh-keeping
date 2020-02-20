package util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

public class NumbersTest {

    @Test
    public void stringToPrice() throws ParseException {
        int i = Numbers.stringToPrice("1.36");
        Assert.assertEquals(136, i);
    }
}