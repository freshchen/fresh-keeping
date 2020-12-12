package com.github.freshchen.keeping.model;

import com.github.freshchen.keeping.marker.ValidMarker;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author darcy
 * @since 2020/06/30
 **/
@Data
public class Group {

    @NotNull(groups = ValidMarker.Update.class)
    private Integer id;
    @NotNull
    private String name;
    @NotNull(groups = ValidMarker.Create.class)
    private String address;
}
