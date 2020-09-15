## Sql必知必会

### Mysql limit

```mysql
SELECT * FROM table LIMIT offset, rows

SELECT * FROM table LIMIT rows OFFSET offset
```

### ORDER BY

-  应该保证 ORDER BY 是 SELECT 语句中最后一条子句。如果它不是最后的子句，将会出现错误消息。

### WHERE

#### 操作符

- = 等于 
-  \> 大于
- < > 不等于
- \ >= 大于等于
- != 不等于
-  !> 不大于
- < 小于 
- BETWEEN 在指定的两个值之间
- <= 小于等于
-  IS NULL 为NULL值
- !< 不小于