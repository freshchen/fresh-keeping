package util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidsTest {

    @Test
    public void email() {
        boolean email = Valids.email("1213123@qq.com");
        Assert.assertTrue(email);
    }

    @Test
    public void email1() {
        boolean email = Valids.email("12131232.com");
        Assert.assertFalse(email);
    }

    @Test
    public void containsNull() {
        boolean b = Valids.containsNull(new Object(), null);
        Assert.assertTrue(b);
    }

    @Test
    public void decimal() {
        Boolean decimal = Valids.decimal("11");
        Assert.assertFalse(decimal);
    }

    @Test
    public void decimal1() {
        Boolean decimal = Valids.decimal("11.1");
        Assert.assertTrue(decimal);
    }
}