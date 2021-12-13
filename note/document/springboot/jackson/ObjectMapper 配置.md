---
begin: 2021-12-07
status: ongoing
rating: 1
---

# ObjectMapper 配置

### 空字段不返回

setSerializationInclusion(NON_ABSENT)

### 兼容空对象返回 例如 空 Object 空 list 不报错
disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

### 不用默认纳秒时间戳

disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

### 未知字段反序列化

disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

## 参考链接


##### 标签
