package com.github.freshchen.keeping.dao.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author darcy
 * @since 2021/10/10
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDetailsPo extends UserPo {

    private CountryPo countryPo;
    private List<UserTagPo> userTagPos;
}
