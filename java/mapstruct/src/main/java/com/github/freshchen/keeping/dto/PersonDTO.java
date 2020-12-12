package com.github.freshchen.keeping.dto;

import lombok.Data;

import java.util.List;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@Data
public class PersonDTO {

    private String name;
    private String realName;
    private List<String> phones;
    // 是深拷贝的
    private List<AddressDTO> addresses;

    private TypeDTO type;
}
