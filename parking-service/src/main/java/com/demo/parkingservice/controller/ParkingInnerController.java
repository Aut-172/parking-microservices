package com.demo.parkingservice.controller;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.demo.common.dto.DecrementRequest;
import com.demo.common.dto.IncrementRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingservice.service.IParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class ParkingInnerController {
    @Autowired
    private IParkingLotService parkingLotService;
    @GetMapping("/parking-lots")
    ParkingLot getById(Long id){
        return parkingLotService.getById(id);
    }
    /**
     * 原子减少车位可用数
     * @param request 包含车位ID
     * @return true 表示更新成功，false 表示车位不足或ID不存在
     */
    @PostMapping("/parking-lots/decrementAvailable")
    public boolean decrementAvailable(@RequestBody DecrementRequest request) {
        // 使用 MyBatis-Plus 的 lambdaUpdate 进行原子更新
        return parkingLotService.lambdaUpdate()
                .setSql("available_spaces = available_spaces - 1")
                .eq(ParkingLot::getId, request.getLotId())
                .ge(ParkingLot::getAvailableSpaces, 1)   // 只有剩余车位>=1时才更新
                .update();
    }
    /**
     * 原子增加车位可用数
     * @param request 包含停车场ID
     * @return true 表示更新成功，false 表示车位不足或ID不存在
     */
    @PostMapping("/parking-lots/incrementAvailable")
    public boolean incrementAvailable(@RequestBody IncrementRequest request) {
        // 使用 MyBatis-Plus 的 lambdaUpdate 进行原子更新
        return parkingLotService.lambdaUpdate()
                .setSql("available_spaces = available_spaces + 1")
                .eq(ParkingLot::getId, request.getLotId())
                .update();
    }
}
