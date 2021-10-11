package com.github.freshchen.keeping.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author darcy
 * @since 2021/10/11
 */
@Service
@Slf4j
public class PostHandler implements UrlHandler {
    @Override
    public String getUrl() {
        return "/api/vi/post";
    }

    @Override
    public void handle() {
        log.info(getUrl() + this.getClass().getSimpleName());
    }
}
