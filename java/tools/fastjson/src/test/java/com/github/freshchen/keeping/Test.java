package com.github.freshchen.keeping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2021/10/11
 */
class Test {

    @org.junit.jupiter.api.Test
    void test1() {
        User user = new User("user", null, null);
        JSONObject o = (JSONObject) JSONObject.toJSON(user);
        Assertions.assertEquals("user", o.getString("name"));
        Assertions.assertNull(o.getString("country"));
        Assertions.assertNull(o.getString("tags"));
    }

    @org.junit.jupiter.api.Test
    void test2() {
        Country country = new Country("Country");
        User user = new User("user", country, null);
        JSONObject o = (JSONObject) JSONObject.toJSON(user);
        Assertions.assertEquals("user", o.getString("name"));
        JSONObject country1 = o.getJSONObject("country");
        Assertions.assertEquals("Country", country1.getString("name"));
        country1.put("name", "Country1");
        Assertions.assertEquals("Country1", country1.getString("name"));
        Assertions.assertEquals("Country1", o.getJSONObject("country").getString("name"));
    }

    @org.junit.jupiter.api.Test
    void test3() {
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        List<Tag> tagArrayList = Lists.newArrayList(tag1, tag2);
        User user = new User("user", null, tagArrayList);
        JSONObject o = (JSONObject) JSONObject.toJSON(user);
        Assertions.assertEquals("user", o.getString("name"));
        JSONArray tags = o.getJSONArray("tags");
        List<Tag> names = IntStream.range(0, tags.size())
            .mapToObj(tags::getJSONObject)
            .map(v -> v.toJavaObject(Tag.class))
            .collect(Collectors.toList());
        System.out.println(names);
        System.out.println(tagArrayList);
    }

}