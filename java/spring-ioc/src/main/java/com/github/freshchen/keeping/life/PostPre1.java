package com.github.freshchen.keeping.life;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class PostPre1 {

    /**
     * 和spring无耦合的介入声明周期
     * 初始化完成包括依赖注入
     * <p>
     * 通过 BeanPostProcessor 实现, 在 before 和 after 中间执行
     * <p>
     * Constructor >> @Autowired >> @PostConstruct
     */
    @PostConstruct
    private void init() {
        System.out.println("PostPre1 PostConstruct");
    }

    /**
     * 可定义多个
     */
    @PostConstruct
    private void init2() {
        System.out.println("PostPre1 PostConstruct init2");
    }

    /**
     * 和spring无耦合的介入声明周期
     * 容器关闭
     * <p>
     */
    @PreDestroy
    private void destroy() {
        System.out.println("PostPre1 PreDestroy");
    }

}
