---
begin: 2021-12-29
status: done
rating: 1
---

# Spring基于注解和配置文件实现SPI机制

## 背景

产品往往存在多种形态，例如简化版、完整版、豪华版等，对于同一个功能我们往往就会有多种实现。

## 分析

对于这种场景，首先应该遵循依赖倒置的原则，抽象出接口，然后提供多种实现。

可以使用策略模式帮助接口消费方找到具有实现类，但存在一定的代码入侵。

比较干净的做法是在 spring 容器启动的时候，只装载当前产品的实现即可。熟悉 Spring 的用户立马会想到 @Condition 机制，@Condition 机制可以控制 Bean 满足一定的条件才装载。使用 springboot 的 @ConditionalOnProperty 注解可以实现上述需求。

代码改动：
```java
public interface Product {}

@ConditionalOnProperty(name = "product.type", havingValue = "standard", matchIfMissing = true)
@Service
public class StandardProduct implements Product {}

@ConditionalOnProperty(name = "product.type", havingValue = "pro", matchIfMissing = false)
@Service
public class ProProduct implements Product {}

@ConditionalOnProperty(name = "product.type", havingValue = "max", matchIfMissing = false)
@Service
public class MaxProduct implements Product {}
```
配置文件改动：
`product.type=pro`

通过 @ConditionalOnProperty 可以达到效果，但有如下问题
- 需要手动维护配置名以及可选配置值
- havingValue 以及 matchIfMissing 配置不正确可能导致装载多个bean，或者没有装载 bean
- spring应用不能使用此注解

 参考 SPI 机制，可以使用约定大于配置的方式解决上述问题，spring 提供的 SPI 使用方式如下，在 META-INF/spring.factories 文件中配置中增加自定义实现

 ```
 org.springframework.boot.autoconfigure.EnableAutoConfiguration=\  
	 com.github.freshchen.keeping.config.WebConfig
 ```

也就是说用接口的全限定类名做配置名，配置值是实现类的全限定类名。这种约定使得配置维护成本减少，且更加清晰。

问题是项目往往接入了配置中心，我们无法修改 META-INF/spring.factories 文件，并且我们配置的类是需要装配到容器内的。

综上，考虑使用注解的方式基于 Spring  @Conditional 机制实现在配置文件中参照 SPI 的方式实现定制化装配。

## 实现

代码改动：
```java
@SpiInterface(defaultSpiService = StandardProduct.class)
public interface Product {}

@SpiService
public class StandardProduct implements Product {}

@SpiService
public class ProProduct implements Product {}

@SpiService
public class MaxProduct implements Product {}
```
配置文件改动：
`com.github.freshchen.keeping.Product=com.github.freshchen.keeping.ProProduct`


要实现上述效果首先我们需要定义两个注解

```java
/**
 * 声明接口为SPI接口，支持通过配置替换实现
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiInterface {

    /**
     * 默认实现，Class 需要使用 SpiService 标识，并且实现 SpiInterface 标识的接口
     * @see SpiService
     */
    Class<?> defaultSpiService();

}
```

```java
/**
 * 标识在 SpiInterface 接口的实现类上
 * @see SpiInterface
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnSpiCondition.class)
@Component
public @interface SpiService {
}
```

注解定义完之后实现 @Condition 功能

```java
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
```



## 参考链接


##### 标签
#springboot #blog 