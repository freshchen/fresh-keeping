package com.github.freshchen.keeping.service;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author darcy
 * @since 2021/10/11
 */
@Validated
public interface UserService {

    void call(@NotBlank String string);
}
