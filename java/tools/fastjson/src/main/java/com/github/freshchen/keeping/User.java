package com.github.freshchen.keeping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author darcy
 * @since 2021/10/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Country country;
    private List<Tag> tags;
}
