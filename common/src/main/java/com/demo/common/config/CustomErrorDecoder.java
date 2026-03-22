package com.demo.common.config;

import com.demo.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        } catch (IOException ignored) {
            // 解析失败，使用默认解码器
        }
        // 对于非业务异常（如404,500等）使用默认解码器
        return defaultDecoder.decode(methodKey, response);
    }


}