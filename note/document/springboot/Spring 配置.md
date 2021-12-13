---
begin: 2021-12-13
status: ongoing
rating: 1
---

# Spring 配置

## @Configuration
- spring 5.2 之后版本建议所有配置主动指定 @Configuration(proxyBeanMethods = false) ，加快启动速度

## @ConfigurationProperties 和 @EnableConfigurationProperties

@ConfigurationProperties 匹配配置的前缀，在 @Configuration 中使用 @EnableConfigurationProperties 引入 @ConfigurationProperties 配置

## @Conditional

@Conditional 根据条件判断是否加载bean，value 需要实现 AnyNestedCondition，全部条件满足才加载

```java
@Configuration(proxyBeanMethods = false)  
@Conditional(FeignCircuitBreakerDisabledConditions.class)  
protected static class DefaultFeignTargeterConfiguration
```

```java
class FeignCircuitBreakerDisabledConditions extends AnyNestedCondition {  

 @ConditionalOnMissingClass("org.springframework.cloud.client.circuitbreaker.CircuitBreaker")  
 static class CircuitBreakerClassMissing {  
  
 }  
 @ConditionalOnProperty(value = "feign.circuitbreaker.enabled", havingValue = "false", matchIfMissing = true)  
 static class CircuitBreakerDisabled {  
 }  
}
```

![](image/Pasted%20image%2020211213202555.png)

## 参考链接


##### 标签
#springboot #interview 