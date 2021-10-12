---
begin: 2021-10-12
status: ongoing
rating: 1
---

# 分页查询 page_token + page_size

https://developers.google.com/shopping-content/guides/reports/paging

Google 在部分搜索接口中分页使用 page_token + page_size 的方式。

### request
其中 pageToken 为上一次查询 response 中的 next_page_token
```json
{
  "query": string,
  "pageSize": integer,
  "pageToken": string
}
```

### response

```json
{
  "results": [
    {}
  ],
  "nextPageToken": string
}
```

### 规则

如果没有指定 page_token，从第一页开始查


## 参考链接




##### 标签
#api #pagination
