package com.demo.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.common.entity.Car;

import java.util.List;

public interface ICarService extends IService<Car> {
    /**
     * 获取用户所有车牌
     */
    List<Car> getUserCars(Long userId);

    /**
     * 添加车牌
     */
    Car addCar(Long userId, String plateNumber);

    /**
     * 删除车牌
     */
    void deleteCar(Long userId, Long carId);

    /**
     * 设置默认车牌
     */
    void setDefaultCar(Long userId, Long carId);

    /**
     * 获取用户默认车牌
     */
    Car getDefaultCar(Long userId);
}
