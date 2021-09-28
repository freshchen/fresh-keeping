package com.github.freshchen.keeping.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.freshchen.keeping.util.Asserts;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptor;


public class CustomEnumSerializer extends JsonSerializer<Enum> {

    private String[] props;

    /**
     * @param prop 属性名
     */
    public CustomEnumSerializer(String prop) {
        Asserts.notBlank(prop);
        this.props = new String[]{prop};
    }

    /**
     * @param props 属性名
     */
    public CustomEnumSerializer(String... props) {
        Asserts.notEmpty(props);
        for (String prop : props) {
            Asserts.notBlank(prop);
        }

        this.props = props;
    }

    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        try {
            for (String prop : props) {
                PropertyDescriptor pd = getPropertyDescriptor(value, prop);
                if (pd != null && pd.getReadMethod() != null) {
                    Method method = pd.getReadMethod();
                    method.setAccessible(true);
                    gen.writeObject(method.invoke(value));
                    return;
                }
            }
            gen.writeString(value.name());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
