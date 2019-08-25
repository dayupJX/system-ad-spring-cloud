package com.system.ad.advice;

import com.system.ad.exception.AdException;
import com.system.ad.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(AdException.class)
    public CommonResponse<String> handleAdException(AdException ex) {
        CommonResponse<String> response = new CommonResponse<String>(-1, "error");
        response.setData(ex.getMessage());
        return response;
    }
}
