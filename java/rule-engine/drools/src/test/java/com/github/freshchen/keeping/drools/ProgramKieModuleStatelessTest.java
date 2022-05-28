package com.github.freshchen.keeping.drools;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Collections;
import java.util.Map;

/**
 * @author darcy
 * @since 2022/5/28
 */
class ProgramKieModuleStatelessTest {

    @Test
    void createJar() {
        String drl = "package fact3\n" +
                "import com.github.freshchen.keeping.drools.Fact\n" +
                "\n" +
                "rule \"rule3\"\n" +
                "when\n" +
                "    $loginFact: Fact(age < 18 || (sub != null && sub.score < 100) || map[\"count\"] < 20)\n" +
                "then\n" +
                "    $loginFact.setRisk(true);\n" +
                "end\n";
        byte[] jar = ProgramKieModuleStateless.createJar("fact3", drl);
        ProgramKieModuleStateless.save(jar);
        StatelessKieSession kieSession = ProgramKieModuleStateless.getKieSession();

        Fact fact = new Fact();
        fact.setAge(100);
        Map<String, Integer> map = Collections.singletonMap("count", 10);
        fact.setMap(map);

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);
    }
}