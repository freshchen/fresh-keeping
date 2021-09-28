package com.github.freshchen.keeping.exception;


import com.github.freshchen.keeping.model.ApiException;
import com.github.freshchen.keeping.model.DomainException;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static com.github.freshchen.keeping.model.Errors.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

/**
 * @author darcy
 * @since 2020/06/13
 **/
@Slf4j
@Component
public class FreshExceptionHandler {

    public static final String NO_STORE = "no-store";
    public static final String BROKEN_PIPE = "Broken pipe";
    public static final String CONNECTION_RESET = "Connection reset by peer";

    public JsonResult<?> handleException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Throwable error) {
        response.setHeader(CACHE_CONTROL, NO_STORE);

        // 获取根异常
        String rootCauseMessage = Optional.ofNullable(ExceptionUtils.getRootCause(error)).orElse(error).getMessage();
        if (StringUtils.containsIgnoreCase(rootCauseMessage, BROKEN_PIPE)
                || StringUtils.containsIgnoreCase(rootCauseMessage, CONNECTION_RESET)) {
            // 客户端连接已断开，无法返回内容
            return null;
        }

        JsonResult<?> result;
        if (error instanceof ApiException) {
            // 处理接入层异常
            result = handleApiException(request, response, (ApiException) error);
        } else if (error instanceof DomainException) {
            // 处理业务层异常
            result = handleDomainException(request, response, (DomainException) error);
        } else if (error instanceof BindException) {
            // 数据绑定的异常（如果参数验证失败也可能抛出该异常）
            result = handleBindException(request, response, (BindException) error);
        } else if (error instanceof ConstraintViolationException) {
            // JSR303 验证失败的异常
            result = handleConstraintViolationException(request, response, (ConstraintViolationException) error);
        } else if (error instanceof MethodArgumentNotValidException) {
            // 一般是业务参数校验异常，但也有可能是服务内部错误
            result = handleInvalidArgumentException(request, response, (MethodArgumentNotValidException) error);
        } else if (error instanceof HttpMediaTypeNotAcceptableException) {
            // 避免 MediaType Not Acceptable 异常
            result = JsonResult.error(BAD_PARAMS.getCode(), "Http MediaType Not Acceptable");
        } else {
            result = handleUnknownException(request, response, error);
        }
        return result;
    }

    public JsonResult handleApiException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         ApiException exception) {
        JsonResult result;
        int code = exception.getCode();
        // 接入层异常设置对应的 Http 状态码
        if (code == BAD_PARAMS.getCode()) {
            response.setStatus(SC_BAD_REQUEST);
        } else if (code == NOT_FOUND.getCode()) {
            response.setStatus(SC_NOT_FOUND);
        } else if (code == FORBIDDEN.getCode()) {
            response.setStatus(SC_FORBIDDEN);
        } else if (code == UNAUTHORIZED.getCode()) {
            response.setStatus(SC_UNAUTHORIZED);
        }
        result = JsonResult.error(code, exception.getMessage());
        return result;
    }

    public JsonResult handleDomainException(HttpServletRequest request,
                                            HttpServletResponse response,
                                            DomainException exception) {
        return JsonResult.error(exception.getCode(), exception.getMessage());
    }

    public JsonResult<?> handleInvalidArgumentException(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        MethodArgumentNotValidException exception) {
        JsonResult<?> result;
        response.setStatus(SC_BAD_REQUEST);
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String defaultMessage = fieldError.getDefaultMessage();
        if (StringUtils.isNotBlank(defaultMessage)) {
            String message = fieldError.getField() + " : " + defaultMessage;
            result = JsonResult.error(BAD_PARAMS.getCode(), message);
        } else {
            result = JsonResult.error(BAD_PARAMS);
        }
        return result;
    }

    public JsonResult<?> handleBindException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             BindException exception) {
        log.debug("数据绑定失败：可能是参数验证失败或类型转换错误", exception);
        response.setStatus(SC_BAD_REQUEST);

        String msg = exception.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .reduce("", (p, c) -> p + " " + c);

        if (StringUtils.isBlank(msg)) {
            return JsonResult.error(BAD_PARAMS);
        } else {
            return JsonResult.error(BAD_PARAMS.getCode(), msg);
        }
    }

    public JsonResult<?> handleConstraintViolationException(HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            ConstraintViolationException exception) {
        log.debug("参数验证失败", exception);
        response.setStatus(SC_BAD_REQUEST);

        String msg = exception.getConstraintViolations()
                .stream()
                .map(e -> e.getPropertyPath().toString() + " " + e.getMessage())
                .reduce("", (p, c) -> p + " " + c);

        if (StringUtils.isBlank(msg)) {
            return JsonResult.error(BAD_PARAMS);
        } else {
            return JsonResult.error(BAD_PARAMS.getCode(), msg);
        }
    }

    public JsonResult<?> handleUnknownException(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Throwable exception) {
        // 位置异常返回 500 状态码
        response.setStatus(SC_INTERNAL_SERVER_ERROR);
        log.error(String.format("未知错误: %s. [URL=%s]",
                exception.getMessage(), request.getRequestURI()), exception);
        // 其他异常都返回「服务器异常」错误
        return JsonResult.error(SERVER_ERROR);
    }
}
