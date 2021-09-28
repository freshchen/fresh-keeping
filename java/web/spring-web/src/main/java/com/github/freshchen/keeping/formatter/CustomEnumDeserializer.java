package com.github.freshchen.keeping.formatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.github.freshchen.keeping.util.Asserts;
import com.github.freshchen.keeping.util.Enums;
import lombok.Setter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


public class CustomEnumDeserializer extends JsonDeserializer<Enum> implements ContextualDeserializer {

    @Setter
    private Class<Enum> enumCls;

    private String[] props;

    /**
     * @param prop 属性名
     */
    public CustomEnumDeserializer(String prop) {
        Asserts.notBlank(prop);
        this.props = new String[]{prop};
    }

    /**
     * @param props 属性名
     */
    public CustomEnumDeserializer(String... props) {
        Asserts.notEmpty(props);
        for (String prop : props) {
            Asserts.notBlank(prop);
        }

        this.props = props;
    }

    @Override
    public Enum deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        String text = parser.getText();

        for (String prop : props) {
            Optional<Enum> enumOpt = Enums.getEnum(enumCls, prop, text);
            if (enumOpt.isPresent()) {
                return enumOpt.get();
            }
        }
        return Stream.of(enumCls.getEnumConstants()).filter(e -> e.name().equals(text)).findFirst().orElse(null);
    }

    @Override
    public JsonDeserializer createContextual(DeserializationContext ctx, BeanProperty property)
            throws JsonMappingException {
        Class rawCls = ctx.getContextualType().getRawClass();
        Asserts.isTrue(rawCls.isEnum());

        Class<Enum> enumCls = (Class<Enum>) rawCls;
        CustomEnumDeserializer clone = new CustomEnumDeserializer(props);
        clone.setEnumCls(enumCls);
        return clone;
    }
}
