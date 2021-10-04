---
begin: 2021-10-04
status: done
rating: 1
---

# count各种用法执行效率

所以结论是：按照效率排序的话，**count(字段)** < **count(主键id)** < **count(1)** 约等于 **count(*)**

针对 InnoDB 引擎 count(*)、count(主键 id) 和 count(1) 都表示返回满足条件的结果集的总行数；而 count (字段），则表示返回满足条件的数据行里面，参数 “字段” 不为 NULL 的总个数

- count(字段）：遍历整张表，需要取值，判断 字段 != null，按行累加；
- count(id) ：遍历整张表，需要取ID，判断 id !=null，按行累加；
- count(1) ： 遍历整张表，【不需要】取值，返回的每一行放一个数字1，按行累加；
- count(*) : 【不需要取字段】，count(*) ，按行累加；

因为count(*) 和 count(1) 不取字段值，减少往 server层的数据返回，所以比其他count(字段)要返回值的【性能】较好



##### 标签
#mysql/sql #interview