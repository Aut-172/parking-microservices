package com.demo.parkingservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.demo.common.dto.DecrementRequest;
import com.demo.common.dto.IncrementRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingservice.service.IParkingLotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/internal")
public class ParkingInnerController {
    @Autowired
    private IParkingLotService parkingLotService;
    @GetMapping("/parking-lots")
    @SentinelResource(value = "getById",
            blockHandler = "handleBlock",      // 限流/熔断时的处理
            fallback = "handleFallback")      // 业务异常时的处理
    ParkingLot getById(Long id){
        return parkingLotService.getById(id);
    }
    // 限流/熔断降级方法（必须与源方法参数相同，最后一个参数为 BlockException）
    public ParkingLot handleBlock(Long id, BlockException ex) {
        log.warn("接口 getParkingLot 被限流或熔断：{}", ex.getClass().getSimpleName());
        throw new RuntimeException("系统繁忙，请稍后再试");
    }
    // 业务异常降级方法（可选）
    public ParkingLot handleFallback(Long id, Throwable ex) {
        log.error("业务异常：{}", ex.getMessage());
        throw new RuntimeException("服务内部错误");
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
