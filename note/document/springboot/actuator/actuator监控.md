---
begin: 2021-11-03
status: half
rating: 1
---

# actuator监控

springboot2.3大版本之后，引入了 k8s 探针和优雅停机。

## 引入 actuator

修改 pom，springboot 版本 2.3.12

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

修改配置

```
management.endpoints.web.base-path=/  
management.endpoints.web.exposure.include=health  
management.endpoints.web.path-mapping.health=/healthz  
management.endpoint.health.probes.enabled=true  
management.endpoint.health.group.readiness.include=readinessState,db  
management.endpoint.shutdown.enabled=true  
  
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/mybatis  
spring.datasource.username=root  
spring.datasource.password=  
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  
  
server.shutdown=graceful  
#要小于 k8s 关机等待时间，默认 30s  
spring.lifecycle.timeout-per-shutdown-phase=20s
```

可访问 endpoint 测试

[http://localhost:8080/healthz/liveness](http://localhost:8080/healthz/liveness)

[http://localhost:8080/healthz/readiness](http://localhost:8080/healthz/readiness)

POST http://localhost:8080/shutdown

### 注意

对于 db 的检查使用 jdbcTemplate 并且没有设置超时时间，可能会因为连接池等待很久至探测超时

## 参考链接
https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html

[Spring Boot 2.3 新特性优雅停机详解 - 冷冷沉思录 - OSCHINA - 中文开源技术交流社区](https://my.oschina.net/giegie/blog/4282378)

##### 标签
#springboot 