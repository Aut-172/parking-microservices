package com.demo.authservice.dto;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginRequest {
    private String phone;    // 手机号
    private String password; // 密码
}