package com.github.freshchen.reflection;

import com.github.freshchen.string.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            throw new IllegalStateException("Reflection get field exception", e);
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        Field field = getField(target.getClass(), fieldName).get();
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Reflection set field value exception", e);
        }
    }

    public static Optional <Object> getFieldValue(Object target, String fieldName) {
        Field field = getField(target.getClass(), fieldName).get();
        field.setAccessible(true);
        try {
            return Optional.ofNullable(field.get(target));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Reflection get field value exception", e);
        }
    }

    public static void invokeSetter(Object target, String fieldName, Object value) {
        Class targetClass = target.getClass();
        Field field = getField(targetClass, fieldName).get();
        String methodName = "set" + Strings.capitalizeFirstLetter(field.getName());
        Class valueClass = value.getClass();
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0] == valueClass) {
                try {
                    method.invoke(target, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException("Reflection invoke setter method exception", e);
                }
            }
        }
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
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException("Reflection invoke setter method exception", e);
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

    public static boolean hasImplementsSpecifiedInterface(Class <?> targetClass, Class <?> checkedInterface) {
        if (checkedInterface.isInterface()) {
            Class[] actualInterfaces = targetClass.getInterfaces();
            for (Class actualInterface : actualInterfaces) {
                if (actualInterface == checkedInterface) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T newInstance(Class <T> clz, Object... paramaters) {
        int length = paramaters.length;
        Class[] paramTypes = new Class[length];
        for (int i = 0; i < length; i++) {
            paramTypes[i] = paramaters[i].getClass();
        }
        Constructor <T> constructor = getConstructor(clz, paramTypes);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(paramaters);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Reflection constructor newInstance failed", e);
        }
    }

    public static <T> Constructor <T> getConstructor(Class <T> clz, Class[] paramTypes) {
        boolean flag = false;
        for (int i = 0; i < paramTypes.length; i++) {
            Optional <Class> unboxingType = unboxing(paramTypes[i]);
            if (unboxingType.isPresent()) {
                paramTypes[i] = unboxingType.get();
                flag = true;
                break;
            }
        }
        Constructor <T> constructor = null;
        try {
            constructor = clz.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            if (flag == false) {
                throw new IllegalStateException("Reflection get constructor failed", e);
            } else {
                getConstructor(clz, paramTypes);
            }
        }
        return constructor;
    }


    public static Optional <Class> unboxing(Class <?> clz) {
        if (clz == Integer.class) {
            return Optional.of(int.class);
        } else if (clz == Byte.class) {
            return Optional.of(byte.class);
        } else if (clz == Short.class) {
            return Optional.of(short.class);
        } else if (clz == Long.class) {
            return Optional.of(long.class);
        } else if (clz == Float.class) {
            return Optional.of(float.class);
        } else if (clz == Double.class) {
            return Optional.of(double.class);
        } else if (clz == Character.class) {
            return Optional.of(char.class);
        } else if (clz == Boolean.class) {
            return Optional.of(boolean.class);
        }
        return Optional.empty();
    }

    private Reflects() {
    }

}
