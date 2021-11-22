package com.github.freshchen.keeping.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

/**
 * @author darcy
 * @since 2021/11/19
 */
@Configuration
public class TtlBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean instanceof Executor) {
//            Executor executor = (Executor) bean;
//            if (TtlExecutors.isTtlWrapper(executor)) {
//                return executor;
//            }
//            return TtlExecutors.getTtlExecutor(executor);
//        }
        return bean;
    }
}
