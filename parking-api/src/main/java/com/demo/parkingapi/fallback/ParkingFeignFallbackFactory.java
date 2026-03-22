package com.demo.parkingapi.fallback;

import com.demo.common.dto.DecrementRequest;
import com.demo.common.dto.IncrementRequest;
import com.demo.common.entity.ParkingLot;
import com.demo.common.exception.BusinessException;
import com.demo.parkingapi.feign.ParkingFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParkingFeignFallbackFactory implements FallbackFactory<ParkingFeignClient> {

    @Override
    public ParkingFeignClient create(Throwable cause) {
        return new ParkingFeignClient() {
            @Override
            public ParkingLot getById(Long id) {
                // 记录详细降级原因
                if (cause instanceof FeignException) {
                    FeignException fe = (FeignException) cause;
                    log.error("调用 parking-service 失败，状态码：{}，原因：{}", fe.status(), fe.getMessage());
                } else {
                    log.error("调用 parking-service 失败，原因：{}", cause.getMessage(), cause);
                }
                // 降级逻辑：抛出业务异常，由调用方全局处理器统一返回
                throw new BusinessException(500,"停车场服务暂不可用，请稍后重试");
            }

            @Override
            public boolean decrementAvailable(DecrementRequest request) {
                if (cause instanceof FeignException) {
                    FeignException fe = (FeignException) cause;
                    log.error("调用 parking-service 失败，状态码：{}，原因：{}", fe.status(), fe.getMessage());
                } else {
                    log.error("调用 parking-service 失败，原因：{}", cause.getMessage(), cause);
                }
                throw new BusinessException(500,"暂时无法修改车位，请稍后重试");
            }

            @Override
            public boolean incrementAvailable(IncrementRequest request) {
                if (cause instanceof FeignException) {
                    FeignException fe = (FeignException) cause;
                    log.error("调用 parking-service 失败，状态码：{}，原因：{}", fe.status(), fe.getMessage());
                } else {
                    log.error("调用 parking-service 失败，原因：{}", cause.getMessage(), cause);
                }
                throw new BusinessException(500,"暂时无法修改车位，请稍后重试");
            }
        };
    }
}