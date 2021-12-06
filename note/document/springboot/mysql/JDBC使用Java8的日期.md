---
begin: 2021-12-03
status: done
rating: 1
---

# JDBC使用Java8的日期

### 错误日志

```
org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.executor.result.ResultMapException: Error attempting to get column 'end_time' from result set. Cause: java.lang.NullPointerException
。。。 。。。

Caused by: java.lang.NullPointerException: null
 at com.mysql.jdbc.JDBC42ResultSet.getObject(JDBC42ResultSet.java:67)
 at com.mysql.jdbc.ResultSetImpl.getObject(ResultSetImpl.java:4659)
 at com.zaxxer.hikari.pool.HikariProxyResultSet.getObject(HikariProxyResultSet.java)
 at org.apache.ibatis.type.LocalDateTimeTypeHandler.getNullableResult(LocalDateTimeTypeHandler.java:38)
 at org.apache.ibatis.type.LocalDateTimeTypeHandler.getNullableResult(LocalDateTimeTypeHandler.java:28)
 at org.apache.ibatis.type.BaseTypeHandler.getResult(BaseTypeHandler.java:81)
 ... 59 common frames omitted
```


如果想要在JDBC中，使用Java8的日期LocalDate、LocalDateTime，则必须要求数据库驱动的版本不能低于4.2,直接打开数据库驱动jar，里面有个META-INF/MANIFEST.MF文件

![](image/Pasted%20image%2020211203192402.png)




## 参考链接

[在JDBC中使用Java8的日期LocalDate、LocalDateTime - zhjh256 - 博客园](https://www.cnblogs.com/zhjh256/p/8372299.html)

[jdk LocalDateTime mybatis 空指针解决办法_haole的专栏-CSDN博客](https://blog.csdn.net/haole/article/details/98845160)


##### 标签
#orm #stackoverflow 
