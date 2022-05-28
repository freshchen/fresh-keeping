package com.github.freshchen.keeping.drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author darcy
 * @since 2022/5/26
 */
public class Rules {

    public static StatelessKieSession getStatelessKieSession(String name) {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer container = kieServices.getKieClasspathContainer();
        return container.newStatelessKieSession(name);
    }
}
