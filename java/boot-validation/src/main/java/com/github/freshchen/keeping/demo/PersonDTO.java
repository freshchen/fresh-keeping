package com.github.freshchen.keeping.demo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author darcy
 * @since 2021/1/8
 **/
@Data
public class PersonDTO {

    @NotNull(message = "更新操作主键Id不能为空", groups = {Group.Update.class})
    @Null(message = "新建操作主键Id必须为空", groups = {Group.Create.class})
    private Integer id;

    @NotBlank(message = "姓名不能为空", groups = {Group.Update.class, Group.Create.class})
    private String name;

    @Min(message = "年龄不能小于0", value = 0)
    @Min(message = "未成年", value = 18, groups = {Group.Api.class})
    @Min(message = "未成年", value = 6, groups = {Group.Admin.class})
    @NotNull(message = "年龄不能为空", groups = {Group.Update.class, Group.Create.class})
    private Integer age;

}
