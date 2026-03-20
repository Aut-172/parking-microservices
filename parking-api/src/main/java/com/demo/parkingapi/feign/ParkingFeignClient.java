package com.demo.parkingapi.feign;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.demo.common.entity.ParkingLot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "parking-service", path = "/api/internal/parking-lots")
public interface ParkingFeignClient {
    @GetMapping
    ParkingLot getById(Long id);
    @PostMapping("/lambdaUpdate")
    LambdaUpdateChainWrapper<ParkingLot> lambdaUpdate();
}
