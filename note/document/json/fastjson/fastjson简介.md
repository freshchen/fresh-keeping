---
begin: 2021-10-12
status: ongoing
rating: 1
---

# fastjson简介

fastjson 是阿里巴巴开源的 [json简介](../json简介.md) 序列化工具，宣传核心竞争力是速度快。

## 简单实战

### 引入依赖

```xml
<dependency>  
	 <groupId>com.alibaba</groupId>  
	 <artifactId>fastjson</artifactId>  
	 <version>${fastjson.version}</version>  
</dependency>
```

### 使用

[代码示例](https://github.com/freshchen/fresh-keeping/blob/master/java/data-format/fastjson/src/test/java/com/github/freshchen/keeping/Test.java)

- [Object -> JSONObject] JSON.toJSON(java.lang.Object)
- [List<Object> -> JSONArray] JSONArray.toJSON(java.lang.Object)
- [String -> JSONObject] JSONObject.parseObject(String)
- [String -> JSONArray] JSONArray.parseArray(String)
- [Object -> String] JSON.toJSONString(Object)
- [List<Object> -> String] JSON.toJSONString(Object)
- [String -> Object] JSONObject.parseObject(String, Class)
- [String -> List<Object>] JSONArray.parseArray(String)


### 注意点
	
JSONArray 不能直接使用 .stream() 操作，正确方式如下
	
```java
JSONArray tags = o.getJSONArray("tags");  
List<Tag> names = IntStream.range(0, tags.size())  
 .mapToObj(tags::getJSONObject)  
 .map(v -> v.toJavaObject(Tag.class))  
 .collect(Collectors.toList());
```
	
	
## 参考链接


##### 标签
#json #format
