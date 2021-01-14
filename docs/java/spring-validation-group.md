# Spring Validation 小技巧之分组校验

## What

数据校验是 web 开发中非常常用的功能， JavaEE 定义了一套通过注解对 Java Bean 进行校验的实现标准 (  JSR-303 )， javax 中的 validation api 模块即是  JSR-303 的接口描述。Hibernate Validator 是 validation api 的官方实现。

Spring Validation 对 Hibernate Validator 进行了封装，在 Controller 层数据绑定过程中能识别校验的注解进行校验并把校验结果绑定到响应中。Spring Validation 一如既往的提供了更便捷的方式帮助开发者注入以及自定义 validator，同时还扩展了标准中的 @Valid 注解，并且在新增的 @Validated 注解中支持了分组校验。

## Why

Spring Validation 加入的这个分组校验解决了什么问题呢？

相信大家在开发过程中经常会遇到一个 DTO 会在多个接口中使用，但是参数校验规则不一样，例如更新接口往往会有数据库主键ID不为空的校验。常见的解决方案：

- 不同校验规则接口定义不同的 DTO 
  - 缺点：增加工作量，且维护成本高
- 将规则不同的字段单独传递
  - 缺点：规则分散，并且如果使用 feginclinet，由于不支持多 body ，不同规则参数多时很麻烦。
- 将不同校验规则的逻辑放到业务代码中
  - 缺点：参数规则变的模棱两可，给接口使用者带来障碍

在这个问题上使用 Spring Validation 的分组校验可以说是两全其美，既不增加工作量，又不降低 DTO 的表达能力。

## How

### 定义规则

```java
@Data
public class PersonDTO {

    @NotNull(message = "更新操作主键Id不能为空", groups = {Group.Update.class})
    @Null(message = "新建操作主键Id必须为空", groups = {Group.Create.class})
    private Integer id;

    @NotBlank(message = "姓名不能为空", groups = {Group.Update.class, Group.Create.class})
    private String name;

    @Min(message = "年龄不能小于0", value = 0)
    @Min(message = "未成年", value = 18, groups = {Group.Minor.class})
    @NotNull(message = "年龄不能为空", groups = {Group.Update.class, Group.Create.class})
    private Integer age;

}
```

### 分组校验

```java
@RestController
public class Person2Controller {

    /**
     * 会执行 创建动作相关空值校验，未成年校验
     *
     * @param person
     */
    @PostMapping("/person")
    public void create(@RequestBody @Validated({Group.Create.class, Group.Minor.class}) PersonDTO person) {
    }

    /**
     * 会执行 默认校验，只有 @Min(message = "年龄不能小于0", value = 0) 会生效
     *
     * @param person
     */
    @PostMapping("/person2")
    public void create2(@RequestBody @Valid PersonDTO person) {
    }

    /**
     * 会执行 更新动作相关空值校验
     *
     * @param person
     */
    @PutMapping("/person")
    public void update(@RequestBody @Validated({Group.Update.class}) PersonDTO person) {
    }

}
```

### 测试

- create 创建校验

![image-20210108172008710](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172008710.png)

- create 创建未成年校验

![image-20210108172203521](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172203521.png)

- Create2 创建校验

![image-20210108172303664](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172303664.png)

- create2 创建不会校验未成年

![image-20210108172327600](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172327600.png)

- create2 创建合法年龄校验

![image-20210108172338740](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172338740.png)

- update 更新校验

![image-20210108172433635](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172433635.png)

- update 更新校验，未成年和合法年龄检测都不会生效

![image-20210108172511548](/Users/chenling/Library/Application Support/typora-user-images/image-20210108172511548.png)

## 最佳实践

如果存在一个 DTO 要在多个接口使用但是校验规则不同的时候推荐使用分组校验，但是一旦使用了分组校验，最好所有校验规则都加上分组信息避免混乱。