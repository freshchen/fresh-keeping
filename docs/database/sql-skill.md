## SQL技巧

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

###  or 还是 union

- 如果where和or涉及不同列，索引会失效
- or和union的区别，具体链接放在末尾。大致意思是: 对于单列来说，用or是没有任何问题的，但是or涉及到多个列的时候，每次select只能选取一个index，如果选择了area，population就需要进行table-scan，即全部扫描一遍，但是使用union就可以解决这个问题，分别使用area和population上面的index进行查询。 但是这里还会有一个问题就是，UNION会对结果进行排序去重，可能会降低一些performance(这有可能是方法一比方法二快的原因），所以最佳的选择应该是两种方法都进行尝试比较。 （stackoverflow链接: https://stackoverflow.com/questions/13750475/sql-performance-union-vs-or）

###  MySQL 日期时间计算函数

时间加 date_add

```sql
set @dt = now();

select date_add(@dt, interval 1 day); -- add 1 day
select date_add(@dt, interval 1 hour); -- add 1 hour
select date_add(@dt, interval 1 minute); -- ...
select date_add(@dt, interval 1 second);
select date_add(@dt, interval 1 microsecond);
select date_add(@dt, interval 1 week);
select date_add(@dt, interval 1 month);
select date_add(@dt, interval 1 quarter);
select date_add(@dt, interval 1 year);

select date_add(@dt, interval -1 day); -- sub 1 day
```

时间减 date_sub 用法和 date_add 相同

### 窗口函数

- [link](https://zhuanlan.zhihu.com/p/92654574)

```sql
<窗口函数> over (partition by <用于分组的列名>
                order by <用于排序的列名>)
```

<窗口函数>的位置，可以放以下两种函数：

-  专用窗口函数，包括后面要讲到的rank, dense_rank, row_number等专用窗口函数。

-  聚合函数，如sum. avg, count, max, min等

因为窗口函数是对where或者group by子句处理后的结果进行操作，所以窗口函数原则上只能写在select子句中。

窗口函数不会改变表中行数

### mod

- 可用于判断奇偶

```sql
where mod(id, 2) = 1
```

### 求百分比

```sql
round(avg(Status!='completed'), 2)
```

### mysql group_concat

mysql的group_concat默认连接长度为1024字符，也就是说你需要连接后的连接超过1024字符，它只会显示这么长，其余部分都会被截取丢掉

```
show variables like "group_concat_max_len";
[mysqld]
group_concat_max_len=102400
```
