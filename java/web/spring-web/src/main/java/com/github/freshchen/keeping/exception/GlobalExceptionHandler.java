package com.github.freshchen.keeping.exception;


import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author darcy
 * @since 2020/06/13
 **/
@ControllerAdvice
@ConditionalOnProperty(name = "fresh.spring.mvc.exception.handler.enable", matchIfMissing = true)
public class GlobalExceptionHandler {

    @Autowired
    private FreshExceptionHandler freshExceptionHandler;

    /**
     * 捕获所有异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public JsonResult<?> handleAllException(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Throwable error) {

        return freshExceptionHandler.handleException(request, response, error);
    }
}
