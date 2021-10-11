package com.github.freshchen.keeping.strategy;

import com.github.freshchen.keeping.common.lib.util.Asserts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author darcy
 * @since 2021/10/11
 */
@Component
public class UrlHandlerContext implements ApplicationContextAware {

    private static Map<String, UrlHandler> urlHandlerMap;

    public static Optional<UrlHandler> getUrlHandler(String url) {
        Asserts.isTrue(StringUtils.isNoneBlank(url));
        return Optional.ofNullable(urlHandlerMap.get(url));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Collection<UrlHandler> handlers = applicationContext.getBeansOfType(UrlHandler.class).values();
        handlers.forEach(urlHandler -> Asserts.isTrue(StringUtils.isNoneBlank(urlHandler.getUrl())));
        Asserts.isTrue(handlers.size() == handlers.stream().map(UrlHandler::getUrl).distinct().count());
        Map<String, UrlHandler> map = handlers.stream()
            .collect(Collectors.toMap(UrlHandler::getUrl, Function.identity()));
        UrlHandlerContext.urlHandlerMap = map;
    }
}
