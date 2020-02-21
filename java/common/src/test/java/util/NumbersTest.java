package util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

public class NumbersTest {

    @Test
    public void yuanToCent() {
        int i = Numbers.yuanToCent("1.367").intValue();
        Assert.assertEquals(136, i);
    }

    @Test
    public void centToYuan() {
        double i = Numbers.centToYuan("13677").doubleValue();
        System.out.println(i);
        Assert.assertEquals(136.77, i, 0.01);
    }

}