---
begin: 2021-12-13
status: half
rating: 1
---

# openfeign使用注意

-   @PathVariable @RequestParam 等注解写明value，例：@PathVariable("orderNo)
    
-   feignclient 接口不要多继承
    
-   feignclient 接口不要 Overrides
    
-   @SpringQueryMap 代替 @QueryMap
    
-   默认 http 超时时间 60 秒，hystrix 超时时间 5 秒。如果有超长耗时接口需要超过 60s，要同时修改 http 和 hystrix 两个超时时间

## 参考链接


##### 标签
#springboot 