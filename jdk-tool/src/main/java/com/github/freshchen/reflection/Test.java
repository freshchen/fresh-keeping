package com.github.freshchen.reflection;


import java.lang.reflect.Method;

/**
 * @author : freshchen
 * <P>Created on 2019-11-20 22:38 </p>
 **/
public class Test {

    public void name(){
    }


    public static void main(String[] args) throws NoSuchMethodException {
        Method method = Test.class.getDeclaredMethod("name");
        System.out.println(method.isAccessible());
    }
}
