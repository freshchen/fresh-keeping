package com.github.freshchen.keeping.life;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 * <p>
 * BeanPostProcessor 实现类以及依赖的 bean 不会织入 aop
 **/
@Component
public class BeanPostProcessor1 implements BeanPostProcessor, Ordered {

    /**
     * PostPre2 这个 bean 的 BeanPostProcessor 不会执行
     */
    @Autowired
    private PostPre2 postPre2;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!beanName.startsWith("org") && !beanName.startsWith("spring")) {
            System.out.println("before1 :" + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!beanName.startsWith("org") && !beanName.startsWith("spring")) {
            System.out.println("after1 :" + beanName);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
