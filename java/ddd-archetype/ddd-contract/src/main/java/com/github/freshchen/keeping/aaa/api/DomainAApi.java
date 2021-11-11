package com.github.freshchen.keeping.aaa.api;

import com.github.freshchen.keeping.aaa.data.DomainADTO;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author darcy
 * @since 2021/11/11
 */
public interface DomainAApi {

    @PostMapping("/api/v1/domainA/create")
    JsonResult<DomainADTO> create();
}
