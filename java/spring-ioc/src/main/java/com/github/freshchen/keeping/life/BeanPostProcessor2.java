package com.github.freshchen.keeping.life;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class BeanPostProcessor2 implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!beanName.startsWith("org") && !beanName.startsWith("spring")) {
            System.out.println("before2 :" + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!beanName.startsWith("org") && !beanName.startsWith("spring")) {
            System.out.println("after2 :" + beanName);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
