---
begin: 2021-11-30
status: done
rating: 1
---

# spring-boot二方库开发

## 配置读取

```java
@Configuration
@PropertySources({
    @PropertySource("classpath:<>.properties"),
})
@ConditionalOnProperty(name = "<>.enable", matchIfMissing = true)
public class PropertiesConfig {  
}
```

resources 目录下写默认的配置 <>.properties

## 自动装配

resources.META-INF 目录下 创建文件 spring.factories 

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\  
 com.github.freshchen.<>.PropertiesConfig
```


## 参考链接


##### 标签
#springboot 