package com.github.freshchen.keeping.life;

import com.github.freshchen.keeping.di.Name;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
/**
 * 不想通过依赖注入的例如工具类可以使用 ApplicationContextAware 获取容器上下文
 */
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static void hello() {
        System.out.println("IocUtils");
        Name name = context.getBean(Name.class);
        name.intro();
    }
}
