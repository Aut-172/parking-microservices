package com.demo.parkingapi.feign;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.demo.common.dto.DecrementRequest;
import com.demo.common.dto.IncrementRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingapi.fallback.ParkingFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "parking-service",
        path = "/api/internal/parking-lots",
        fallbackFactory = ParkingFeignFallbackFactory.class)
public interface ParkingFeignClient {
    @GetMapping
    ParkingLot getById(@RequestParam("id")Long id);
    @PostMapping("/decrementAvailable")
    boolean decrementAvailable(@RequestBody DecrementRequest request);
    @PostMapping("/incrementAvailable")
    boolean incrementAvailable(@RequestBody IncrementRequest request);
}
