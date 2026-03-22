package com.demo.common.config;

import com.demo.common.exception.BusinessException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper;
    // 通过构造函数传入 ObjectMapper
    public CustomErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // 忽略未知属性，防止响应中有 timestamp 等额外字段
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        // 尝试从响应体中提取业务错误信息
        try {
            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            com.demo.common.dto.Response<?> errorResp = objectMapper.readValue(body, com.demo.common.dto.Response.class);
            // 假设服务端统一返回格式为 Response，其中 code != 200 表示错误
            if (errorResp.getCode() != 200) {
                return new BusinessException(errorResp.getCode(), errorResp.getMsg());
            }
        } catch (IOException e) {
            // 解析失败，使用默认解码器
           log.error("Feign解析响应体失败 ", e);
        }
        // 对于非业务异常（如404,500等）使用默认解码器
        return defaultDecoder.decode(methodKey, response);
    }


}