package com.demo.parkingservice.handler;

import com.demo.common.dto.Response;
import com.demo.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ServiceUnavailableException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<?>> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        Response<?> error = Response.error(e.getCode(), e.getMessage());
        // 根据业务码决定 HTTP 状态码，通常业务异常可用 400
        HttpStatus status = HttpStatus.valueOf(e.getCode() >= 400 && e.getCode() < 600 ? e.getCode() : 400);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Response<?>> handleServiceUnavailable(ServiceUnavailableException e) {
        log.error("服务不可用：{}", e.getMessage());
        Response<?> error = Response.error(503, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception e) {
        log.error("系统内部错误", e);
        Response<?> error = Response.error(500, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}