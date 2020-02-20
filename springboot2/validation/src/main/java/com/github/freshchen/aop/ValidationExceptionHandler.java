package com.github.freshchen.aop;

import model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author darcy
 * @since 2020/02/20
 **/
@ControllerAdvice
public class ValidationExceptionHandler {

    /**
     * 仅用做记录，就不分层了
     *
     * @param request
     * @param response
     * @param error
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handleException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException error) {
        return ResponseEntity.status(400).body(handleMethodArgumentNotValidException(request, response, error));

    }

    private List<Result<?>> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException error) {
        List<ObjectError> allErrors = error.getBindingResult().getAllErrors();
        return allErrors.stream().map(objectError -> {
            Result<?> errorMsg = new Result<>();
            errorMsg.setErrCode(Optional.of(400));
            errorMsg.setSuccess(false);
            errorMsg.setErrMessage(Optional.ofNullable(objectError.getDefaultMessage()));
            return errorMsg;
        }).collect(Collectors.toList());
    }
}
