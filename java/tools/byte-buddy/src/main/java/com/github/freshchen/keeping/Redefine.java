package com.github.freshchen.keeping;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author darcy
 * @since 2022/2/12
 */
public class Redefine {

    public static void main(String[] args) {
        ByteBuddyAgent.install();
        new ByteBuddy()
            .redefine(O1.class)
            .method(ElementMatchers.named("hello"))
            .intercept(MethodDelegation.to(O1Hello.class))
            .make()
            .load(O1.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        O1 o1 = new O1();
        o1.setName("name");
        String hello = o1.hello();
        System.out.println(hello);

    }
}
