package com.demo.userservice.controller;

import com.demo.userservice.dto.CarAddRequest;
import com.demo.common.entity.Car;
import com.demo.userservice.service.ICarService;
import com.demo.common.dto.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private ICarService carService;

    /**
     * 获取当前用户的所有车牌
     */
    @GetMapping
    public Response<List<Car>> getUserCars() {
        Long userId = getCurrentUserId();
        List<Car> cars = carService.getUserCars(userId);
        return Response.success(cars);
    }

    /**
     * 获取当前用户的默认车牌
     */
    @GetMapping("/default")
    @PreAuthorize("isAuthenticated()")
    public Response<Car> getDefaultCar() {
        Long userId = getCurrentUserId();
        Car car = carService.getDefaultCar(userId);
        return Response.success(car);
    }

    /**
     * 添加车牌
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Response<Car> addCar(@Valid @RequestBody CarAddRequest request) {
        Long userId = getCurrentUserId();
        try {
            Car car = carService.addCar(userId, request.getPlateNumber());
            return Response.success(car);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 删除车牌
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Response<Void> deleteCar(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        try {
            carService.deleteCar(userId, id);
            return Response.success();
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 设置默认车牌
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("isAuthenticated()")
    public Response<Void> setDefaultCar(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        try {
            carService.setDefaultCar(userId, id);
            return Response.success();
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 从 SecurityContext 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new RuntimeException("用户未登录");
        }
        return (Long) authentication.getPrincipal();
    }
}