package com.github.freshchen.reflection;

import com.github.freshchen.string.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : freshchen
 * <P>Created on 2019-11-20 23:48 </p>
 **/
public class Reflects {

    public static Optional <Field> getField(Class <?> clz, String fieldName) {
        try {
            return Optional.ofNullable(clz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Reflection get field exception -" +
                    e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        Field field = getField(target.getClass(), fieldName).get();
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Reflection set field value exception -" +
                    e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static Object getFieldValue(Object target, String fieldName) {
        Field field = getField(target.getClass(), fieldName).get();
        field.setAccessible(true);
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Reflection get field value exception -" +
                    e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static boolean invokeSetter(Object target, String fieldName, Object value) {
        Class targetClass = target.getClass();
        Field field = getField(targetClass, fieldName).get();
        String methodName = "set" + Strings.capitalizeFirstLetter(field.getName());
        Class valueClass = value.getClass();
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0] == valueClass) {
                try {
                    return Objects.nonNull(method.invoke(target, value));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Reflection invoke setter method access exception -" +
                            e.getClass().getName() + ": " + e.getMessage());
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Reflection invoke setter method operation exception -" +
                            e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        return false;
    }

    public static Optional <Object> invokeGetter(Object target, String fieldName) {
        Class targetClass = target.getClass();
        Field field = getField(targetClass, fieldName).get();
        String methodName = "get" + Strings.capitalizeFirstLetter(field.getName());
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) && method.getParameterCount() == 0) {
                try {
                    return Optional.ofNullable(method.invoke(target));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Reflection invoke setter method access exception -"
                            + e.getClass().getName() + ": " + e.getMessage());
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Reflection invoke setter method operation exception -" +
                            e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isToStringMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "toString".equals(m.getName()) && m.getParameterCount() == 0).orElse(false);
    }

    public static boolean isHashCodeMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "hashCode".equals(m.getName()) && m.getParameterCount() == 0).orElse(false);
    }

    public static boolean isEqualsMethod(Method method) {
        return Optional.ofNullable(method).map(m -> "equals".equals(m.getName()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == Object.class).orElse(false);
    }

    private Reflects() {
    }

}
