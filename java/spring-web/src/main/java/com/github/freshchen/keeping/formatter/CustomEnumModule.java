package com.github.freshchen.keeping.formatter;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.freshchen.keeping.util.Asserts;


public class CustomEnumModule extends SimpleModule {

    /**
     * @param prop 属性名
     */
    public CustomEnumModule(String prop) {
        Asserts.notEmpty(prop);
        addDeserializer(Enum.class, new CustomEnumDeserializer(prop));
        addSerializer(Enum.class, new CustomEnumSerializer(prop));
    }

    /**
     * @param props 属性名
     */
    public CustomEnumModule(String... props) {
        Asserts.notEmpty(props);
        addDeserializer(Enum.class, new CustomEnumDeserializer(props));
        addSerializer(Enum.class, new CustomEnumSerializer(props));
    }
}
