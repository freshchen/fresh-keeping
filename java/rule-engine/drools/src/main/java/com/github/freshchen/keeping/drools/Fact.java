package com.github.freshchen.keeping.drools;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author darcy
 * @since 2022/4/28
 * fact
 */
@Data
@NoArgsConstructor
public class Fact {

    private Integer age;
    private String name;
    private LocalDateTime time;
    private SubFact sub;
    private Map<String, Integer> map;
    private Boolean risk;

}
