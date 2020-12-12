package com.github.freshchen.keeping.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumbersTest {

    @Test
    public void yuanToCent() {
        int i = Numbers.yuanToCent("1.367").intValue();
        Assertions.assertEquals(136, i);
    }

    @Test
    public void centToYuan() {
        double i = Numbers.centToYuan("13677").doubleValue();
        System.out.println(i);
        Assertions.assertEquals(136.77, i, 0.01);
    }

}
