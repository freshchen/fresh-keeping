package com.github.freshchen.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * @author : freshchen
 * <P>Created on 2019-11-20 23:48 </p>
 **/
public class Reflects {

    public static boolean isToStringMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "toString".equals(m.getName()) && m.getParameterCount() == 0).orElse(false);
    }

    public static boolean isHashCodeMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "hashCode".equals(m.getName()) && m.getParameterCount() == 0).orElse(false);
    }

    public static boolean isEqualsMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "equals".equals(m.getName()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == Object.class).orElse(false);
    }

    public static void setAccessible(Object methodAndField) {
        if (methodAndField instanceof Method) {
            Method method = (Method) methodAndField;
            if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else if (methodAndField instanceof Field) {
            Field field = (Field) methodAndField;
            if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) && !field.isAccessible()) {
                field.setAccessible(true);
            }
        }
    }

    public static boolean setFiledValue(Object target, Field field) {
        return true;
    }


    private Reflects() {
    }
}
