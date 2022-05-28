package com.github.freshchen.keeping.drools;

import com.google.common.collect.Lists;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.StatelessKieSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author darcy
 * @since 2022/5/26
 */
class Fact1Test {

    @Test
    @DisplayName("简单判断")
    public void test1() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact1");

        Fact fact = new Fact();
        fact.setAge(10);
        fact.setName("darcy");

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);
    }

    @Test
    @DisplayName("增加 global")
    public void test2() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact1");
        ArrayList<String> list = new ArrayList<>();
        // 除了集合，global 也可以用来注入 bean
        kieSession.setGlobal("list", list);

        Fact fact = new Fact();
        fact.setAge(10);
        fact.setName("darcy");

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);
        System.out.println("debug list: " + list);

    }

    @Test
    @DisplayName("事件")
    public void test3() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact1");
        ArrayList<String> list = new ArrayList<>();
        // 除了集合，global 也可以用来注入 bean
        kieSession.setGlobal("list", list);
        // 打印所有工作内存事件
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        // 打印议程事件
        kieSession.addEventListener(new DebugAgendaEventListener());

        Fact fact = new Fact();
        fact.setAge(10);
        fact.setName("darcy");

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);

    }

    @Test
    @DisplayName("debug日志事件")
    public void test4() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact1");
        ArrayList<String> list = new ArrayList<>();
        // 除了集合，global 也可以用来注入 bean
        kieSession.setGlobal("list", list);

        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newConsoleLogger(kieSession);

        Fact fact = new Fact();
        fact.setAge(10);
        fact.setName("darcy");

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);

        logger.close();
    }

    @Test
    @DisplayName("批量")
    public void test5() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact2");
        Fact fact = new Fact();
        fact.setAge(10);
        fact.setName("darcy");
        Fact fact1 = new Fact();
        fact1.setAge(23);
        fact1.setName("darcy");

        ArrayList<Fact> facts = Lists.newArrayList(fact, fact1);

        System.out.println("debug before: " + facts);
        kieSession.execute(facts);
        System.out.println("debug after: " + facts);

    }

    @Test
    @DisplayName("嵌套的对象")
    public void test6() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact2");
        Fact fact = new Fact();
        fact.setAge(100);
        SubFact subFact = new SubFact();
        subFact.setScore(5);
        fact.setSub(subFact);

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);
    }

    @Test
    @DisplayName("集合属性")
    public void test7() {
        StatelessKieSession kieSession = Rules.getStatelessKieSession("fact2");
        Fact fact = new Fact();
        fact.setAge(100);
        Map<String, Integer> map = Collections.singletonMap("count", 10);
        fact.setMap(map);

        System.out.println("debug before: " + fact);
        kieSession.execute(fact);
        System.out.println("debug after: " + fact);
    }

}