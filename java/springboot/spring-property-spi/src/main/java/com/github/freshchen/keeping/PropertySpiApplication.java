package com.github.freshchen.keeping;

import com.github.freshchen.keeping.test.Notification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darcy
 * @since 2021/9/15
 */
@SpringBootApplication
public class PropertySpiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PropertySpiApplication.class, args);
        Notification bean = run.getBean(Notification.class);
        bean.send("aaa");
        run.close();
    }
}
