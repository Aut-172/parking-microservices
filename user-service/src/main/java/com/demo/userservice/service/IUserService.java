package com.demo.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.demo.common.dto.RegisterRequest;
import com.demo.common.entity.User;

public interface IUserService extends IService<User> {
    User getByPhone(String phone);

    User register(RegisterRequest request);


}
