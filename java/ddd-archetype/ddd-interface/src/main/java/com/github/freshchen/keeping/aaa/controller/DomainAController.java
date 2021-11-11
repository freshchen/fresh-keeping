package com.github.freshchen.keeping.aaa.controller;

import com.github.freshchen.keeping.aaa.api.DomainAApi;
import com.github.freshchen.keeping.aaa.data.DomainADTO;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/11/11
 */
@RestController
public class DomainAController implements DomainAApi {

    @Override
    public JsonResult<DomainADTO> create() {
        return null;
    }
}
