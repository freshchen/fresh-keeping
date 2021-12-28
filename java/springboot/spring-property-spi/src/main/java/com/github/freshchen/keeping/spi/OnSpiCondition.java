package com.github.freshchen.keeping.spi;

import com.github.freshchen.keeping.annotations.SpiInterface;
import com.github.freshchen.keeping.annotations.SpiService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author darcy
 * @since 2021/12/28
 */
public class OnSpiCondition implements Condition {

    @SneakyThrows
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationMetadata annotationMetadata = (AnnotationMetadata) metadata;
        String curClassName = annotationMetadata.getClassName();
        if (SpiService.class.getName().equals(curClassName)) {
            return true;
        }
        String defaultClassName = null;
        String spiInterfaceName = null;
        Class<?> spiInterfaceClass = null;
        for (String interfaceName : annotationMetadata.getInterfaceNames()) {
            Class<?> interfaceClass = Objects.requireNonNull(context.getClassLoader()).loadClass(interfaceName);
            if (Objects.nonNull(interfaceClass)) {
                SpiInterface annotation = interfaceClass.getAnnotation(SpiInterface.class);
                if (Objects.nonNull(annotation)) {
                    Class<?> defaultImplClass = annotation.defaultSpiService();
                    boolean assignableFrom = interfaceClass.isAssignableFrom(defaultImplClass);
                    Assert.isTrue(assignableFrom, "@Spi默认类未实现相关接口 " + interfaceName);
                    defaultClassName = defaultImplClass.getName();
                    spiInterfaceName = interfaceName;
                    spiInterfaceClass = interfaceClass;
                }
            }
        }
        Assert.isTrue(StringUtils.isNoneBlank(defaultClassName, spiInterfaceName)
            && Objects.nonNull(spiInterfaceClass), "@SpiService 未实现任何 @Spi 接口");
        String propertyValue = context.getEnvironment().getProperty(spiInterfaceName, defaultClassName);
        Class<?> propertyClass = Objects.requireNonNull(context.getClassLoader()).loadClass(propertyValue);
        boolean propertyCorrect = StringUtils.isNotBlank(propertyValue)
            && spiInterfaceClass.isAssignableFrom(propertyClass);
        Assert.isTrue(propertyCorrect, "配置文件 spi 配置错误 " + spiInterfaceName);
        return propertyValue.equals(curClassName);
    }
}
