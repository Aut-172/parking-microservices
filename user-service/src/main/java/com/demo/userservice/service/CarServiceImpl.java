package com.demo.userservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.demo.common.entity.Car;
import com.demo.common.util.RedisUtil;
import com.demo.userservice.mapper.CarMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.demo.common.constant.RedisConstant.*;

@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public List<Car> getUserCars(Long userId) {
        List<Car> cars = redisUtil.get(CACHE_KEY_CAR+userId, List.class);
        if(cars==null){
            cars = lambdaQuery().eq(Car::getUserId, userId)
                    .orderByDesc(Car::getIsDefault) // 默认车牌排前面
                    .orderByDesc(Car::getCreateTime)
                    .list();
            redisUtil.set(CACHE_KEY_CAR+userId,cars, CAR_EXPIRE);
        }
        return cars;
    }

    @Override
    @Transactional
    public Car addCar(Long userId, String plateNumber) {
        // 检查车牌是否已存在（全局唯一）
        boolean exists = lambdaQuery().eq(Car::getPlateNumber, plateNumber).exists();
        if (exists) {
            throw new RuntimeException("车牌号已被绑定");
        }

        Car car = new Car();
        car.setUserId(userId);
        car.setPlateNumber(plateNumber);
        car.setIsDefault(0); // 新增时不设为默认
        save(car);
        redisUtil.del(CACHE_KEY_CAR+userId);
        return car;
    }

    @Override
    @Transactional
    public void deleteCar(Long userId, Long carId) {
        Car car = getById(carId);
        if (car == null || !car.getUserId().equals(userId)) {
            throw new RuntimeException("车牌不存在或无权限删除");
        }
        removeById(carId);
        redisUtil.del(CACHE_KEY_CAR+userId);
        redisUtil.del(CACHE_KEY_DETAIL+userId);
        // 如果删除的是默认车牌，无需额外操作，后续设置默认即可
    }

    @Override
    @Transactional
    public void setDefaultCar(Long userId, Long carId) {
        Car car = getById(carId);
        if (car == null || !car.getUserId().equals(userId)) {
            throw new RuntimeException("车牌不存在或无权限操作");
        }

        // 先将该用户所有车牌设为非默认
        lambdaUpdate()
                .eq(Car::getUserId, userId)
                .set(Car::getIsDefault, 0)
                .update();

        // 再将指定车牌设为默认
        lambdaUpdate()
                .eq(Car::getId, carId)
                .set(Car::getIsDefault, 1)
                .update();
        redisUtil.del(CACHE_KEY_CAR+userId);
        redisUtil.del(CACHE_KEY_DETAIL+userId);
    }

    @Override
    public Car getDefaultCar(Long userId) {
        Car car = redisUtil.get(CACHE_KEY_CAR_DEFAULT+userId, Car.class);
        if(car==null){
            car = lambdaQuery()
                    .eq(Car::getUserId, userId)
                    .eq(Car::getIsDefault, 1)
                    .one();
            redisUtil.set(CACHE_KEY_CAR_DEFAULT+userId,car,CAR_EXPIRE);
        }
        return car;
    }
}
