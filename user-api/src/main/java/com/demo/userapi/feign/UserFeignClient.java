package com.demo.userapi.feign;

import com.demo.common.entity.User;
import com.demo.common.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// user-api/src/main/java/com/demo/user/api/feign/UserFeignClient.java
@FeignClient(name = "user-service", path = "/api/internal/users")
public interface UserFeignClient {
    @GetMapping("/phone")
    User getByPhone(String phone);
    @PostMapping("/register")
    User register(@RequestBody RegisterRequest registerRequest);
}