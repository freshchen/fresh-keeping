package com.github.freshchen.string;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringsTest {

    @Test
    public void capitalizeFirstLetter() {
        Assert.assertEquals("Adasd", Strings.capitalizeFirstLetter("adasd"));
        Assert.assertEquals("Adasd", Strings.capitalizeFirstLetter("Adasd"));
    }
}