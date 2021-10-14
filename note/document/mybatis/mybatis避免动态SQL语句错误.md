---
begin: 2021-10-15
status: ongoing
rating: 1
---

# mybatis避免动态SQL语句错误

一个臭名昭著的动态 SQL 问题，由于使用了动态SQL加条件语句，会导致生成错误SQL的严重问题如下
```
SELECT * FROM BLOG
WHERE

SELECT * FROM BLOG
WHERE
AND a=b
```

## 解决方法

可以使用 trim 标签对SQL进行修剪

### 查询
可以使用 where 标签，其效果相当于
```
<trim prefix="WHERE" prefixOverrides="AND |OR "> ... </trim>
```

### 更新
可以使用 set 标签，其效果相当于
```
<trim prefix="SET" suffixOverrides=","> ... </trim>
```

### 插入
```
insert into user  
<trim prefix="(" suffix=")" suffixOverrides=",">  
 	user_name, email, gender, country_id,  
</trim>  
<trim prefix="values (" suffix=")" suffixOverrides=",">  
 	#{userPo.userName}, #{userPo.email}, #{userPo.gender}, #{userPo.countryId},  
</trim>
```

## 参考链接


##### 标签
#orm #database #interview 