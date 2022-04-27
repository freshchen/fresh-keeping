package com.github.freshchen.keeping;

import lombok.Data;

/**
 * @author darcy
 * @since 2022/2/12
 */
@Data
public class O1 {

    private String name;

    public String hello() {
        return "hello";
    }

}
