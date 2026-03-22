package com.demo.userservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.demo.common.dto.RegisterRequest;
import com.demo.common.entity.User;
import com.demo.common.exception.BusinessException;
import com.demo.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User getByPhone(String phone) {
        return lambdaQuery().eq(User::getPhone, phone).one();
    }

    @Override
    public User register(RegisterRequest request) {
        // 1. 检查手机号是否已存在
        User existingUser = lambdaQuery().eq(User::getPhone, request.getPhone()).one();
        if (existingUser != null) {
            throw new BusinessException(409,"手机号已注册");
        }

        // 2. 创建新用户并填充属性
        User user = new User();
        user.setPhone(request.getPhone());

        // 密码加密
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 处理昵称
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        } else {
            // 自动生成默认昵称：用户 + 手机号后四位
            String phone = request.getPhone();
            String suffix = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
            user.setNickname("用户" + suffix);
        }

        // 设置默认值
        user.setRole(0);    // 普通用户
        user.setPoints(0);  // 初始积分

        // 3. 保存用户
        save(user); // 保存后 user 对象会包含自动生成的主键 ID
        return user;
    }

}