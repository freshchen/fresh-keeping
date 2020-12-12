package com.github.freshchen.keeping.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChineseToPinyinsTest {

    @Test
    public void toPinyin() {
        Assertions.assertEquals("nihao", ChineseToPinyins.toPinyin("你好"));
        Assertions.assertEquals("niahao1", ChineseToPinyins.toPinyin("你a好1"));
    }

    @Test
    public void toPinyinFirstSpell() {
        Assertions.assertEquals("nh", ChineseToPinyins.toPinyinFirstSpell("你好"));
        Assertions.assertEquals("nah1", ChineseToPinyins.toPinyinFirstSpell("你a好1"));
    }
}
