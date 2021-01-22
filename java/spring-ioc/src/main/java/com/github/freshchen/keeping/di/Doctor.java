package com.github.freshchen.keeping.di;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class Doctor {

    /**
     * By type
     */
    @Autowired
    @Getter
    private User user;
}
