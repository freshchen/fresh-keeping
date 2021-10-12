package com.github.freshchen.keeping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2021/10/11
 */
class Test {

    @org.junit.jupiter.api.Test
    @DisplayName("[Object -> JSONObject] JSONObject.toJSON(Object)")
    void test1() {
        User user = new User("user", null, null);
        JSONObject o = (JSONObject) JSONObject.toJSON(user);
        Assertions.assertEquals("user", o.getString("name"));
        Assertions.assertNull(o.getString("country"));
        Assertions.assertNull(o.getString("tags"));
        System.out.println(o.toJSONString());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[List<Object> -> JSONArray] JSONArray.toJSON(Object)")
    void test2() {
        User user = new User("user", null, null);
        User user1 = new User("user1", null, null);
        JSONArray array = (JSONArray) JSONArray.toJSON(Lists.newArrayList(user, user1));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[String -> JSONObject] JSONObject.parseObject(String)")
    void test3() {
        String json = "{\n" +
            "  \"company\": \"Google\",\n" +
            "  \"website\": \"http://www.google.com/\",\n" +
            "  \"address\": {\n" +
            "    \"line1\": \"111 8th Ave\",\n" +
            "    \"line2\": \"4th Floor\",\n" +
            "    \"state\": \"NY\",\n" +
            "    \"city\": \"New York\",\n" +
            "    \"zip\": \"10011\"\n" +
            "  }\n" +
            "}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        Assertions.assertEquals("Google", jsonObject.getString("company"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[String -> JSONArray] JSONArray.parseArray(String)")
    void test4() {
        String json = "[{\"company\": \"Google\"},{\"company\": \"Google\"}]";
        JSONArray array = JSONArray.parseArray(json);
        Assertions.assertEquals(2, array.size());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[Object -> String] JSON.toJSONString(Object)")
    void test5() {
        User user = new User("user", null, null);
        String s = JSON.toJSONString(user);
        System.out.println(s);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[List<Object> -> String] JSON.toJSONString(Object)")
    void test6() {
        User user = new User("user", null, null);
        User user1 = new User("user1", null, null);
        String s = JSON.toJSONString(Lists.newArrayList(user, user1));
        System.out.println(s);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[String -> Object] JSONObject.parseObject(String, Class)")
    void test7() {
        String json = "{\"name\":\"user\"}";
        User user = JSONObject.parseObject(json, User.class);
        System.out.println(user);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("[String -> List<Object>] JSONArray.parseArray(String)")
    void test8() {
        String json = "[{\"name\":\"user\"},{\"name\":\"user\"}]";
        List<User> users = JSONArray.parseArray(json, User.class);
        System.out.println(users);
    }

    @org.junit.jupiter.api.Test
    void test90() {
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
    void test91() {
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