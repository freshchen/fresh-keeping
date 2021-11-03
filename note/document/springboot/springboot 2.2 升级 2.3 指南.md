---
begin: 2021-10-28
status: ongoing
rating: 1
---

# springboot 升级指南

维护升级中遇到的问题

## 注意事项

-   如果用 Gradle 最低版本要求 6.3+
    
-   如果用 Jetty 最低版本要求 9.4.22+
    
-   不再支持 Maven Exec Plugin
    

# 通用修改

## 版本选择

使用目前最新GA版本，首先修改 pom

```xml
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>2.3.12.RELEASE</version>
	<relativePath/> <!-- lookup parent from repository -->
</parent>
```

## 引入validation

spring-boot-starter-web 去掉了 validation 依赖，需要手动引入

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```


## mapstruct 
springboot 2.3.12 内置 lombok 版本升级到了 1.18.20。可能出现以下错误

```log
No property named "name" exists in source parameter(s). Did you mean "null"?
```
原因：mapstruct 在 lombok 前执行了，因此需要控制顺序
解决办法：指定 lombok 在 mapstruct-processor 前
```xml
<plugin>  
	 <groupId>org.apache.maven.plugins</groupId>  
	 <artifactId>maven-compiler-plugin</artifactId>  
	 <version>${maven-compiler-plugin.version}</version>  
	 <configuration>  
	 <source>1.8</source>  
	 <target>1.8</target>  
	 <annotationProcessorPaths>  
		 <path>  
			 <groupId>org.projectlombok</groupId>  
			 <artifactId>lombok</artifactId>  
			 <version>${lombok.version}</version>  
		 </path>  
		 <path>  
			 <groupId>org.mapstruct</groupId>  
			 <artifactId>mapstruct-processor</artifactId>  
			 <version>${mapstruct.version}</version>  
		 </path>  
	 </annotationProcessorPaths>  
	 </configuration>  
</plugin>
```

# springboot 2.2 -> springboot 2.3
## 参考链接
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes

##### 标签
#springboot