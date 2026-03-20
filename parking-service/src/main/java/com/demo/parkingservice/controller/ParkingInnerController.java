package com.demo.parkingservice.controller;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingservice.service.IParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class ParkingInnerController {
    @Autowired
    private IParkingLotService parkingLotService;
    @GetMapping("/parking-lots")
    ParkingLot getById(Long id){
        return parkingLotService.getById(id);
    }
    @GetMapping("/parking-lots/lambdaUpdate")
    LambdaUpdateChainWrapper<ParkingLot> lambdaUpdate(){
        return parkingLotService.lambdaUpdate();
    }
}
