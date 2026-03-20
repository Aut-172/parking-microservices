package com.demo.authservice.controller;

import com.demo.authservice.dto.LoginRequest;
import com.demo.common.dto.RegisterRequest;
import com.demo.common.util.JwtUtils;
import com.demo.common.dto.Response;
import com.demo.common.entity.User;
import com.demo.userapi.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    PasswordEncoder passwordEncoder;
    /**
     * 用户登录接口
     * @param loginRequest 手机号和密码
     * @return 包含 JWT 的响应
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody LoginRequest loginRequest) {
        // 1. 根据手机号查询用户
        User user = userFeignClient.getByPhone(loginRequest.getPhone());

        // 2. 验证用户是否存在及密码是否正确
        if (user == null) {
            return Response.error("手机号或密码错误");
        }

        // 使用 BCrypt 验证密码（需注入 PasswordEncoder）
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Response.error("手机号或密码错误");
        }

        // 3. 构建 JWT 负载（仅包含必要信息，避免敏感字段）

        String  token = JwtUtils.createUserJWT(user);

        // 4. 返回 token
        return Response.success(token);

    }
    /**
     * 用户注册接口
     * @param  registerRequest 注册请求
     * @return Jwt
     */
    @PostMapping("/register")
    public Response<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 调用 Service 执行注册
            User user = userFeignClient.register(registerRequest);
            String token = JwtUtils.createUserJWT(user);
            return Response.success("注册成功",token);
        } catch (IllegalArgumentException e) {
            // 业务逻辑异常（如手机号已存在）
            return Response.error(e.getMessage());
        } catch (Exception e) {
            // 其他系统异常
            return Response.error("注册失败，请稍后重试");
        }
    }

}
