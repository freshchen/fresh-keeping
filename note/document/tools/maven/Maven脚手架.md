---
begin: 2021-12-16
status: ongoing
rating: 1
---

# Maven脚手架

命令汇总
```
mvn archetype:create-from-project
```

### 1书写项目骨架

例如一个包含 interface 和 client 两个模块的 sdk

![](image/Pasted%20image%2020211216184421.png)

### 2 创建模版

```
mvn archetype:create-from-project
```

### 3复制出工程

复制出生上述命令生产成的 archetype 目录，全部移动到另一个文件夹下，并进入
![](image/Pasted%20image%2020211216185520.png)



进入复制出的 archetype 目录下：

archetype/src/main/resources/META-INF/maven/archetype-metadata.xml



## 参考链接

[Fetching Title#5gm4](http://maven.apache.org/archetype/maven-archetype-plugin/specification/archetype-metadata.html)

##### 标签
#tools 