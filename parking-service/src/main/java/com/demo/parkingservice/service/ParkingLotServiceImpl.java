package com.demo.parkingservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.common.exception.BusinessException;
import com.demo.common.util.RedisUtil;
import com.demo.parkingservice.dto.ParkingLotCreateRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.parkingservice.mapper.ParkingLotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

import static com.demo.common.constant.RedisConstant.*;

@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements IParkingLotService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 用于批量删除

    @Override
    public IPage<ParkingLot> getParkingLotPage(int pageNum, int pageSize) {
        // 1. 构建缓存 key
        String key = CACHE_KEY_PAGE + pageNum + ":" + pageSize;

        // 2. 尝试从缓存获取
        IPage<ParkingLot> page = redisUtil.get(key, IPage.class);
        if (page != null) {
            return page; // 缓存命中，直接返回
        }

        // 3. 缓存未命中，查询数据库
        Page<ParkingLot> dbPage = new Page<>(pageNum, pageSize);
        page = lambdaQuery()
                .orderByDesc(ParkingLot::getCreateTime)
                .page(dbPage);

        // 4. 将结果存入缓存（即使结果为空也缓存，防止缓存穿透，可选）
        if (page != null) {
            redisUtil.set(key, page, PAGE_EXPIRE);
        }else{
            redisUtil.set(key,page,NULL_EXPIRE);
        }

        return page;
    }

    @Override
    public ParkingLot getParkingLotDetail(Long id) {
        // 1. 构建缓存 key
        String key = CACHE_KEY_DETAIL + id;

        // 2. 尝试从缓存获取
        ParkingLot parkingLot = redisUtil.get(key, ParkingLot.class);
        if (parkingLot != null) {
            return parkingLot;
        }

        // 3. 缓存未命中，查询数据库
        parkingLot = getById(id);

        // 4. 存入缓存（如果存在）
        if (parkingLot != null) {
            redisUtil.set(key, parkingLot, DETAIL_EXPIRE);
        }else{
            redisUtil.set(key,parkingLot,NULL_EXPIRE);
        }

        return parkingLot;
    }

    @Override
    @Transactional
    public ParkingLot createParkingLot(ParkingLotCreateRequest request) {
        // 校验剩余车位不能大于总车位
        if (request.getAvailableSpaces() > request.getTotalSpaces()) {
            throw new BusinessException(409,"剩余车位数不能大于总车位数");
        }

        ParkingLot lot = new ParkingLot();
        lot.setName(request.getName());
        lot.setAddress(request.getAddress());
        lot.setTotalSpaces(request.getTotalSpaces());
        lot.setAvailableSpaces(request.getAvailableSpaces());
        lot.setLatitude(request.getLatitude());
        lot.setLongitude(request.getLongitude());
        lot.setFeeRate(request.getFeeRate());
        // version 默认为0

        save(lot);
        return lot;
    }

    @Override
    @Transactional
    public ParkingLot updateParkingLot(Long id, ParkingLotCreateRequest request) {
        ParkingLot lot = getById(id);
        if (lot == null) {
            throw new BusinessException(403,"停车场不存在");
        }

        // 校验剩余车位不能大于总车位
        if (request.getAvailableSpaces() > request.getTotalSpaces()) {
            throw new BusinessException(409,"剩余车位数不能大于总车位数");
        }

        lot.setName(request.getName());
        lot.setAddress(request.getAddress());
        lot.setTotalSpaces(request.getTotalSpaces());
        lot.setAvailableSpaces(request.getAvailableSpaces());
        lot.setLatitude(request.getLatitude());
        lot.setLongitude(request.getLongitude());
        lot.setFeeRate(request.getFeeRate());

        updateById(lot);  // 乐观锁自动处理 version

        // 清理缓存
        clearDetailCache(id);
        clearPageCache();

        return lot;
    }

    @Override
    @Transactional
    public ParkingLot increaseAvailableSpaces(Long id, int increment) {
        ParkingLot lot = getById(id);
        if (lot == null) {
            throw new BusinessException(404,"停车场不存在");
        }

        int newSpaces = lot.getAvailableSpaces() + increment;
        // 不能超过总车位
        if (newSpaces > lot.getTotalSpaces()) {
            throw new BusinessException(409,"增加后剩余车位数不能超过总车位数");
        }

        lot.setAvailableSpaces(newSpaces);
        updateById(lot);  // 乐观锁自动处理 version

        // 清理缓存
        clearDetailCache(id);
        clearPageCache();

        return lot;
    }

    @Override
    public IPage<ParkingLot> searchParkingLots(String keyword, int pageNum, int pageSize) {
        // 构建缓存 key（包含关键词、页码和大小）
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String key = CACHE_KEY_PAGE + "search:" + safeKeyword + ":" + pageNum + ":" + pageSize;

        // 尝试从缓存获取
        IPage<ParkingLot> page = redisUtil.get(key, IPage.class);
        if (page != null) {
            return page;
        }

        // 缓存未命中，查询数据库
        Page<ParkingLot> dbPage = new Page<>(pageNum, pageSize);
        boolean hasKeyword = StringUtils.hasText(safeKeyword);
        page = lambdaQuery()
                .like(hasKeyword, ParkingLot::getName, safeKeyword)
                .or(hasKeyword, w -> w.like(ParkingLot::getAddress, safeKeyword))
                .orderByDesc(ParkingLot::getCreateTime)
                .page(dbPage);

        // 存入缓存
        if (page != null) {
            redisUtil.set(key, page, SEARCH_PAGE_EXPIRE);
        }else{
            redisUtil.set(key,page,NULL_EXPIRE);
        }

        return page;
    }

    // 删除指定停车场的详情缓存
    private void clearDetailCache(Long id) {
        String key = CACHE_KEY_DETAIL + id;
        redisUtil.del(key);
    }

    // 删除所有分页缓存（包括普通分页和搜索分页）
    private void clearPageCache() {
        // 获取所有以 "parking:page:" 开头的 key
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PAGE + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
