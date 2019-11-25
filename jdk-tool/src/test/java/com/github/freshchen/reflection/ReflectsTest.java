package com.github.freshchen.reflection;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @anthor freshchen
 */
public class ReflectsTest {

    @Test
    public void isToStringMethod() throws NoSuchMethodException {
        Method method1 = Object.class.getMethod("toString");
        Assert.assertTrue(Reflects.isToStringMethod(method1));
        Method method2 = Object.class.getMethod("equals", Object.class);
        Assert.assertFalse(Reflects.isToStringMethod(method2));
        Assert.assertFalse(Reflects.isToStringMethod(null));
    }

    @Test
    public void isHashCodeMethod() throws NoSuchMethodException {
        Method method1 = Object.class.getMethod("hashCode");
        Assert.assertTrue(Reflects.isHashCodeMethod(method1));
        Method method2 = Object.class.getMethod("equals", Object.class);
        Assert.assertFalse(Reflects.isHashCodeMethod(method2));
        Assert.assertFalse(Reflects.isHashCodeMethod(null));
    }

    @Test
    public void isEqualsMethod() throws NoSuchMethodException {
        Method method1 = Object.class.getMethod("equals", Object.class);
        Assert.assertTrue(Reflects.isEqualsMethod(method1));
        Method method2 = Object.class.getMethod("hashCode");
        Assert.assertFalse(Reflects.isEqualsMethod(method2));
        Assert.assertFalse(Reflects.isEqualsMethod(null));
    }

    @Test
    public void setFiledValue()  {
        String s = "";
        Reflects.setFieldValue(s, "hash", 12);
        Assert.assertEquals(12, Reflects.getFieldValue(s, "hash").get());
    }

    @Test
    public void getFiledValue() {
        String s = "";
        Reflects.setFieldValue(s, "hash", 12);
        Assert.assertEquals(12, Reflects.getFieldValue(s, "hash").get());
    }
}