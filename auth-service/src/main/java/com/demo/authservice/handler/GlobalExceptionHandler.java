package com.demo.authservice.handler;

import com.demo.common.dto.Response;
import com.demo.common.exception.BusinessException;
import feign.FeignException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public Response<?> handleServiceUnavailable(ServiceUnavailableException e) {
        return Response.error(503, e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public Response<?> handleFeignException(FeignException e) {
        return Response.error(500, "服务间调用失败");
    }

    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        return Response.error(500, "系统内部错误");
    }
}