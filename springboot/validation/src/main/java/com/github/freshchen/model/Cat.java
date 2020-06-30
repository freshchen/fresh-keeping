package com.github.freshchen.model;

import com.github.freshchen.custom.Age;
import com.github.freshchen.marker.ValidMarker;
import lombok.Data;

/**
 * @author darcy
 * @since 2020/06/30
 **/
@Data
public class Cat {

    @Age(groups = ValidMarker.Create.class)
    private Integer age;
}
