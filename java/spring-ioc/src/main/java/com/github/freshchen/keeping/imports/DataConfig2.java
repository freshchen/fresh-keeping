package com.github.freshchen.keeping.imports;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2021/2/5
 **/
@Configuration
public class DataConfig2 {

    @Bean
    public DataService dataService2() {
        return new DataService2();
    }
}
