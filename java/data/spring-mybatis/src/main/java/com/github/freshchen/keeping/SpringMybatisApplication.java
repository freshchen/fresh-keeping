package com.github.freshchen.keeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@SpringBootApplication
//@MapperScan("com.github.freshchen.keeping.dao.mapper")
public class SpringMybatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMybatisApplication.class, args);
    }
}
