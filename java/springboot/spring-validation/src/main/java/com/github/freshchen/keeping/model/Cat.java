package com.github.freshchen.keeping.model;

import com.github.freshchen.keeping.custom.Age;
import com.github.freshchen.keeping.marker.ValidMarker;
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
