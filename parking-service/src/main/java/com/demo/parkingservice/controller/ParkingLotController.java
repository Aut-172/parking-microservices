package com.demo.parkingservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.common.dto.Response;
import com.demo.parkingservice.dto.IncreaseSpacesRequest;
import com.demo.parkingservice.dto.ParkingLotCreateRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingservice.service.IParkingLotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {
    @Autowired
    private IParkingLotService parkingLotService;

    /**
     * 分页查询停车场列表
     * @param pageNum  页码，默认1
     * @param pageSize 每页条数，默认10
     * @return 分页结果
     */
    @GetMapping
    public Response<IPage<ParkingLot>> listParkingLots(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ParkingLot> page = parkingLotService.getParkingLotPage(pageNum, pageSize);
        return Response.success(page);
    }

    /**
     * 获取停车场详情
     * @param id 停车场ID
     * @return 停车场信息
     */
    @GetMapping("/{id}")
    public Response<ParkingLot> getParkingLotDetail(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getParkingLotDetail(id);
        if (parkingLot == null) {
            return Response.error("停车场不存在");
        }
        return Response.success(parkingLot);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Response<ParkingLot> createParkingLot(@Valid @RequestBody ParkingLotCreateRequest request) {
        try {
            ParkingLot lot = parkingLotService.createParkingLot(request);
            return Response.success(lot);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<ParkingLot> updateParkingLot(@PathVariable Long id,
                                                 @Valid @RequestBody ParkingLotCreateRequest request) {
        try {
            ParkingLot lot = parkingLotService.updateParkingLot(id, request);
            return Response.success(lot);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<Void> deleteParkingLot(@PathVariable Long id) {
        boolean removed = parkingLotService.removeById(id);
        if (!removed) {
            return Response.error("删除失败，停车场不存在");
        }
        return Response.success();
    }

    @PatchMapping("/{id}/increase-spaces")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<ParkingLot> increaseSpaces(@PathVariable Long id,
                                               @Valid @RequestBody IncreaseSpacesRequest request) {
        try {
            ParkingLot lot = parkingLotService.increaseAvailableSpaces(id, request.getIncrement());
            return Response.success(lot);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 搜索停车场（按名称或地址模糊查询）
     * @param keyword  搜索关键字（必填）
     * @param pageNum  页码，默认1
     * @param pageSize 每页条数，默认10
     * @return 分页结果
     */
    @GetMapping("/search")
    public Response<IPage<ParkingLot>> searchParkingLots(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ParkingLot> page = parkingLotService.searchParkingLots(keyword, pageNum, pageSize);
        return Response.success(page);
    }
}
