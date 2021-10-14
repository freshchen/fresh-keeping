---
begin: 2021-10-14
status: ongoing
rating: 1
---

# mybatis ${} 和 #{}

## 区别
### #{} 先预处理 sql 再赋值
- `select * from user where name = #{}`
- sql 预处理，使用 ? 占位符
- `select * from user where name = ?`
- 赋值
- `select * from user where name = 'aaa'`

### ${} 先赋值再预处理 sql
- `select * from ${}`
- 赋值
- `select * from 'user'`

## 总结

-   #{} 方式能够很大程度防止 sql 注入。
-   $ 方式无法防止 sql 注入。
-   $ 方式一般用于传入数据库对象，例如传入表名.
-   一般能用 # 的就不用 $.

## 参考链接

https://blog.csdn.net/j04110414/article/details/78914787

##### 标签
#orm #database #interview 