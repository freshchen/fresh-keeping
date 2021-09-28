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
    private TypeDTO type;
}
