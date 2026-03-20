package com.demo.parkingservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.parkingservice.dto.ParkingLotCreateRequest;
import com.demo.common.entity.ParkingLot;

public interface IParkingLotService extends IService<ParkingLot> {
    /**
     * 分页查询停车场列表
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    IPage<ParkingLot> getParkingLotPage(int pageNum, int pageSize);

    /**
     * 根据ID查询停车场详情
     * @param id 停车场ID
     * @return 停车场实体，不存在返回null
     */
    ParkingLot getParkingLotDetail(Long id);

    /**
     * 新增停车场
     * @param request 请求参数
     * @return 保存后的停车场对象
     */
    ParkingLot createParkingLot(ParkingLotCreateRequest request);

    /**
     * 更新停车场
     * @param id 停车场ID
     * @param request 请求参数
     * @return 更新后的对象
     */
    ParkingLot updateParkingLot(Long id, ParkingLotCreateRequest request);

    /**
     * 增加空余车位
     * @param id 停车场ID
     * @param increment 增加数量
     * @return 更新后的对象
     */
    ParkingLot increaseAvailableSpaces(Long id, int increment);

    /**
     * 按关键字搜索停车场（名称或地址模糊匹配）
     * @param keyword  搜索关键字
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    IPage<ParkingLot> searchParkingLots(String keyword, int pageNum, int pageSize);
}
