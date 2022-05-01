package com.github.freshchen.keeping.spring.drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darcy
 * @since 2022/4/28
 */
@SpringBootApplication
public class DroolsApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DroolsApp.class);
        LoginService bean = run.getBean(LoginService.class);

        KieContainer container = KieServices.get().getKieClasspathContainer();
        KieSession session = container.newKieSession("all-rules");
        session.setGlobal("loginService", bean);
        session.getAgenda().getAgendaGroup("gruop-1").setFocus();
        LoginForm loginForm = new LoginForm();
        loginForm.setAge(10);
        loginForm.setName("name");

        session.insert(loginForm);
        session.fireAllRules();
        session.dispose();
        System.out.println(loginForm);
    }
}
