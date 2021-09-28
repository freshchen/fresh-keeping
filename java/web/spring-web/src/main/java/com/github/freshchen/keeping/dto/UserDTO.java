package com.github.freshchen.keeping.dto;

import com.github.freshchen.keeping.common.lib.marker.Create;
import com.github.freshchen.keeping.common.lib.marker.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.OffsetDateTime;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@Data
@ApiModel("用户传输对象")
public class UserDTO {

    @NotNull(groups = {Update.class}, message = "id 不能为空")
    @Null(groups = {Create.class}, message = "不支持")
    @ApiModelProperty("用户数据库主键")
    private Long id;
    @NotNull(groups = {Create.class}, message = "姓名不能为空")
    private String name;
    @NotNull(groups = {Create.class}, message = "姓别不能为空")
    private Gender gender;
    private Integer age;
    private String email;
    private OffsetDateTime birth;

}
