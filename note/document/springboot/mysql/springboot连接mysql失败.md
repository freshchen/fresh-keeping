---
begin: 2021-11-17
status: done
rating: 1
---

# 数据库连接失败

## CLIENT_PLUGIN_AUTH is required

mysql 驱动 错误。 驱动8.0之后版本不适配 mysql5.7之前版本

- mysql 版本 >= 5.7
	- spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
	- mysql-connector-java version > 8.0.*
- mysql 版本 < 5.7
	- spring.datasource.driver-class-name=com.mysql.jdbc.Driver
	- mysql-connector-java version > 5.1.*

## 参考链接


##### 标签
#stackoverflow