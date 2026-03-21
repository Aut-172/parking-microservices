package com.demo.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@ComponentScan(basePackages = {"com.demo.userservice", "com.demo.common"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);

    }

}
