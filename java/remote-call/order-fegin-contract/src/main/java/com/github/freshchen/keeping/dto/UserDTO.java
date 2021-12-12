package com.github.freshchen.keeping.dto;

import com.github.freshchen.keeping.common.lib.marker.Create;
import com.github.freshchen.keeping.common.lib.marker.Update;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@Data
public class UserDTO {

    @NotNull(groups = {Update.class}, message = "id 不能为空")
    @Null(groups = {Create.class}, message = "不支持")
    private Long id;
    @NotNull(groups = {Create.class}, message = "姓名不能为空")
    private String name;
    private Integer age;
    private String email;

}
