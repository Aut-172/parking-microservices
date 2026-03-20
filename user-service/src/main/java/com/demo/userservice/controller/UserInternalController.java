package com.demo.userservice.controller;

import com.demo.common.entity.User;
import com.demo.common.dto.RegisterRequest;
import com.demo.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/users")
public class UserInternalController {

    @Autowired
    private IUserService userService;

    @GetMapping("/phone")
    public User getByPhone(String phone) {
        return userService.getByPhone(phone);
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }
}