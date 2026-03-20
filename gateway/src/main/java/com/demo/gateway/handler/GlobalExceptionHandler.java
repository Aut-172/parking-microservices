package com.demo.gateway.handler;

import com.demo.common.dto.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@Order(-1) // 确保优先级高于默认处理器
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = determineStatus(ex);
        response.setStatusCode(status);

        // 构建统一的 Response 对象
        Response<?> errorResponse;
        if (ex instanceof ResponseStatusException) {
            errorResponse = Response.error(status.value(), ex.getMessage());
        } else {
            errorResponse = Response.error(status.value(), "网关处理异常：" + ex.getMessage());
        }

        // 序列化并写入响应
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("序列化异常响应失败", e);
            // 兜底返回
            byte[] fallback = "{\"code\":500,\"msg\":\"内部错误\"}".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(fallback);
            return response.writeWith(Mono.just(buffer));
        }
    }

    /**
     * 根据异常类型映射 HTTP 状态码
     */
    private HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
        }
        // 可根据实际异常类型扩展
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}