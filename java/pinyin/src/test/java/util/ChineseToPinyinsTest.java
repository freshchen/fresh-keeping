package util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChineseToPinyinsTest {

    @Test
    public void toPinyin() {
        Assert.assertEquals("nihao", ChineseToPinyins.toPinyin("你好"));
        Assert.assertEquals("niahao1", ChineseToPinyins.toPinyin("你a好1"));
    }

    @Test
    public void toPinyinFirstSpell() {
        Assert.assertEquals("nh", ChineseToPinyins.toPinyinFirstSpell("你好"));
        Assert.assertEquals("nah1", ChineseToPinyins.toPinyinFirstSpell("你a好1"));
    }
}