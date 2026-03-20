package com.demo.common.dto;

import lombok.Data;

/*
注册请求参数
 */
@Data
public class RegisterRequest {
    private String phone;      // 手机号（必填）
    private String password;   // 密码（必填）
    private String nickname;   // 昵称（可选，若不传则自动生成默认昵称）
}
