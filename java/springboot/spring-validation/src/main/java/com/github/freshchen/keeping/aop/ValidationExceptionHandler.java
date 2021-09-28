package com.github.freshchen.keeping.aop;

import com.github.freshchen.keeping.model.JsonResult;
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

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handleException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException error) {
        return ResponseEntity.status(400).body(handleMethodArgumentNotValidException(request, response, error));

    }

    private List<JsonResult<?>> handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException error) {
        List<ObjectError> allErrors = error.getBindingResult().getAllErrors();
        return allErrors.stream().map(objectError -> {
            JsonResult<?> errorMsg = new JsonResult<>();
            errorMsg.setErrCode(Optional.of(400));
            errorMsg.setSuccess(false);
            errorMsg.setErrMessage(Optional.ofNullable(objectError.getDefaultMessage()));
            return errorMsg;
        }).collect(Collectors.toList());
    }
}
