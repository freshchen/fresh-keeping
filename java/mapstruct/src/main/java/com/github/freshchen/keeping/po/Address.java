package com.github.freshchen.keeping.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2020/8/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String city;
    private String street;
}
