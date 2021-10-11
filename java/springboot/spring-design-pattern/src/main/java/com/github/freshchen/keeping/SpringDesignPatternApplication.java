package com.github.freshchen.keeping;

import com.github.freshchen.keeping.strategy.UrlHandler;
import com.github.freshchen.keeping.strategy.UrlHandlerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

/**
 * @author darcy
 * @since 2021/9/15
 */
@SpringBootApplication
public class SpringDesignPatternApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDesignPatternApplication.class, args);
        Optional<UrlHandler> get = UrlHandlerContext.getUrlHandler("get");
        Optional<UrlHandler> post = UrlHandlerContext.getUrlHandler("/api/vi/post");
        Optional<UrlHandler> get1 = UrlHandlerContext.getUrlHandler("/api/vi/get");
        System.out.println();
    }
}
