package com.github.freshchen.keeping.util;


import com.github.freshchen.keeping.common.lib.util.Valids;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidsTest {

    @Test
    public void email() {
        boolean email = Valids.email("1213123@qq.com");
        Assertions.assertTrue(email);
    }

    @Test
    public void email1() {
        boolean email = Valids.email("12131232.com");
        Assertions.assertFalse(email);
    }

    @Test
    public void containsNull() {
        boolean b = Valids.containsNull(new Object(), null);
        Assertions.assertTrue(b);
    }

    @Test
    public void decimal() {
        Boolean decimal = Valids.decimal("11");
        Assertions.assertFalse(decimal);
    }

    @Test
    public void decimal1() {
        Boolean decimal = Valids.decimal("11.1");
        Assertions.assertTrue(decimal);
    }

    @Test
    public void phone() {
        boolean phone = Valids.phone("11111111111");
        Assertions.assertTrue(phone);
    }

    @Test
    public void phone1() {
        boolean phone = Valids.phone("1111111111");
        Assertions.assertFalse(phone);
    }

    @Test
    public void number() {
        boolean number = Valids.number("1");
        Assertions.assertTrue(number);
    }

    @Test
    public void number1() {
        boolean number = Valids.number("12s1");
        Assertions.assertFalse(number);
    }

    @Test
    public void ipv4() {
        boolean b = Valids.ipv4("192.168.0.1");
        Assertions.assertTrue(b);
    }

    @Test
    public void ipv41() {
        boolean b = Valids.ipv4("192.256.0.1");
        Assertions.assertFalse(b);
    }
}
